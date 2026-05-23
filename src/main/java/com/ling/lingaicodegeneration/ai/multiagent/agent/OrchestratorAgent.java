package com.ling.lingaicodegeneration.ai.multiagent.agent;

import cn.hutool.json.JSONUtil;
import com.ling.lingaicodegeneration.ai.multiagent.model.*;
import com.ling.lingaicodegeneration.core.CodeFileSaver;
import com.ling.lingaicodegeneration.model.enums.ChatHistoryMessageTypeEnum;
import com.ling.lingaicodegeneration.monitor.MonitorContext;
import com.ling.lingaicodegeneration.monitor.MonitorContextHolder;
import com.ling.lingaicodegeneration.service.ChatHistoryService;
import dev.langchain4j.internal.Json;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@Slf4j
@Component
public class OrchestratorAgent implements Agent<String, Flux<String>> {

    @Resource private RequirementAgent requirementAgent;
    @Resource private PlannerAgent      plannerAgent;
    @Resource private AssetAgent        assetAgent;
    @Resource private CodeGenAgent      codeGenAgent;
    @Resource private ReviewAgent       reviewAgent;
    @Resource private RefineAgent       refineAgent;
    @Resource private ChatHistoryService chatHistoryService;
    @Resource(name = "imageSearchExecutor")
    private ExecutorService imageSearchExecutor;

    @Override
    public Flux<String> execute(String message, AgentContext ctx) {
        return Flux.create(sink -> {
            Thread vt = Thread.startVirtualThread(() -> {
                log.info("OrchestratorAgent starting, appId: {}", ctx.appId());
                ReviewReport finalReview = null;
                try {
                    sink.next(event("workflow_start", null));

                    // ── 1. RequirementAgent + AssetAgent in parallel ──────────────────
                    // Both depend only on originalPrompt; run concurrently.
                    // Each future catches internally so allOf() never fails.
                    // MonitorContext is set per-thread inside the lambda.
                    CompletableFuture<RequirementSpec> reqFuture = CompletableFuture.supplyAsync(() -> {
                        MonitorContextHolder.setContext(monCtx(ctx, "RequirementAgent"));
                        try {
                            return requirementAgent.execute(message,
                                    ctx.withAgentName("RequirementAgent"));
                        } catch (Exception e) {
                            log.warn("RequirementAgent failed, using fallback: {}", e.getMessage());
                            return RequirementSpec.builder()
                                    .codeGenType(com.ling.lingaicodegeneration.model.enums.CodeGenTypeEnum.HTML)
                                    .projectDescription(message)
                                    .features(List.of())
                                    .techConstraints(List.of())
                                    .complexity(Complexity.MEDIUM)
                                    .build();
                        } finally {
                            MonitorContextHolder.clearContext();
                        }
                    }, imageSearchExecutor);

                    CompletableFuture<String> assetFuture = CompletableFuture.supplyAsync(() -> {
                        MonitorContextHolder.setContext(monCtx(ctx, "AssetAgent"));
                        try {
                            return assetAgent.execute(message, ctx.withAgentName("AssetAgent"));
                        } catch (Exception e) {
                            log.warn("AssetAgent failed, returning empty images: {}", e.getMessage());
                            return "";
                        } finally {
                            MonitorContextHolder.clearContext();
                        }
                    }, imageSearchExecutor);

                    CompletableFuture.allOf(reqFuture, assetFuture).join();
                    RequirementSpec spec   = reqFuture.join();
                    String          images = assetFuture.join();

                    sink.next(event("requirement_done", "type=" + spec.getCodeGenType()));
                    sink.next(event("asset_done",
                            images.isBlank() ? "no-images" : "images-found"));

                    // ── 2. PlannerAgent ───────────────────────────────────────────────
                    sink.next(event("planning_start", null));
                    TaskGraph plan;
                    MonitorContextHolder.setContext(monCtx(ctx, "PlannerAgent"));
                    try {
                        plan = plannerAgent.execute(spec, ctx.withAgentName("PlannerAgent"));
                    } finally {
                        MonitorContextHolder.clearContext();
                    }
                    sink.next(event("planning_done", "retries=" + plan.suggestedRetries()));

                    // ── 3. CodeGenAgent — stream tokens to SSE client in real-time ────
                    sink.next(event("code_gen_start", null));
                    String outputDir = CodeFileSaver.resolveOutputDir(spec.getCodeGenType(), ctx.appId());
                    CodeGenInput codeInput = new CodeGenInput(spec, images, plan);
                    MonitorContextHolder.setContext(monCtx(ctx, "CodeGenAgent"));
                    try {
                        Flux<String> codeFlux = codeGenAgent.execute(codeInput,
                                ctx.withAgentName("CodeGenAgent"));
                        codeFlux.doOnNext(token -> sink.next(event("code_token", token)))
                                .blockLast(Duration.ofMinutes(5));
                    } finally {
                        MonitorContextHolder.clearContext();
                    }
                    sink.next(event("code_gen_done", null));

                    // ── 4. Review → Refine loop ───────────────────────────────────────
                    int maxRetries = plan.suggestedRetries();
                    for (int attempt = 0; attempt <= maxRetries; attempt++) {
                        sink.next(event("review_start",
                                "attempt=" + (attempt + 1) + "/" + (maxRetries + 1)));

                        ReviewInput reviewInput = new ReviewInput(outputDir, spec);
                        MonitorContextHolder.setContext(monCtx(ctx, "ReviewAgent"));
                        try {
                            finalReview = reviewAgent.execute(reviewInput,
                                    ctx.withAgentName("ReviewAgent"));
                        } finally {
                            MonitorContextHolder.clearContext();
                        }

                        if (finalReview.isDegraded()) {
                            log.warn("ReviewAgent returned degraded report, skipping Refine, appId: {}",
                                    ctx.appId());
                            sink.next(event("review_degraded", "review-unavailable"));
                            break;
                        }

                        log.info("Review attempt {}/{}: passed={}, score={}",
                                attempt + 1, maxRetries + 1,
                                finalReview.getPassed(), finalReview.getScore());
                        sink.next(event("review_done",
                                "score=" + finalReview.getScore()
                                        + ",passed=" + finalReview.getPassed()));

                        if (finalReview.getPassed() || attempt >= maxRetries) break;

                        // Refine — stream tokens to SSE client just like CodeGenAgent
                        sink.next(event("refine_start", "attempt=" + (attempt + 1)));
                        List<String> targetFiles = extractTargetFiles(finalReview);
                        RefinementPlan refinePlan = new RefinementPlan(
                                targetFiles, plan.reviewFocusAreas(), attempt + 1);
                        RefineInput refineInput = new RefineInput(outputDir, finalReview, refinePlan);

                        MonitorContextHolder.setContext(monCtx(ctx, "RefineAgent"));
                        try {
                            Flux<String> refineFlux = refineAgent.execute(refineInput,
                                    ctx.withAgentName("RefineAgent"));
                            refineFlux.doOnNext(token -> sink.next(event("code_token", token)))
                                      .blockLast(Duration.ofMinutes(5));
                            sink.next(event("refine_done", "attempt=" + (attempt + 1)));
                        } catch (Exception e) {
                            // RefineAgent failure: consume retry budget and continue to next Review.
                            log.warn("RefineAgent attempt {} failed, continuing to next review: {}",
                                    attempt + 1, e.getMessage());
                            sink.next(event("refine_failed", "attempt=" + (attempt + 1)));
                        } finally {
                            MonitorContextHolder.clearContext();
                        }
                    }

                    // ── 5. Persist final ReviewReport to chat_history ─────────────────
                    if (finalReview != null) {
                        try {
                            chatHistoryService.addStructuredMessage(
                                    ctx.appId(),
                                    Json.toJson(finalReview),
                                    ChatHistoryMessageTypeEnum.AI_REVIEW_REPORT.getValue(),
                                    ctx.userId()
                            );
                        } catch (Exception e) {
                            log.warn("Failed to save ReviewReport to chat_history, appId: {}: {}",
                                    ctx.appId(), e.getMessage());
                        }
                    }

                    sink.next(event("workflow_done", null));
                    log.info("OrchestratorAgent completed, appId: {}", ctx.appId());
                    sink.complete();

                } catch (Exception e) {
                    log.error("OrchestratorAgent fatal error, appId: {}: {}",
                            ctx.appId(), e.getMessage(), e);
                    sink.next(event("workflow_error", truncate(e.getMessage(), 200)));
                    sink.error(e);
                }
            });

            // When the SSE client disconnects, interrupt the orchestration virtual thread so it
            // stops starting new agent calls immediately.
            // NOTE(P8): This stops the virtual thread but the langchain4j streaming thread
            // (langchain4j-OpenAI-*) continues reading until the HTTP response body closes.
            // Spring RestClient uses SimpleClientHttpRequestFactory (java.net.HttpURLConnection)
            // which does not respond to Thread.interrupt() on a different thread.
            // TODO(P8): Replace SimpleClientHttpRequestFactory with OkHttp or JDK HttpClient
            //           for full HTTP-level cancellation on client disconnect.
            sink.onCancel(() -> {
                log.info("SSE client disconnected, interrupting orchestration, appId: {}",
                        ctx.appId());
                vt.interrupt();
            });
        });
    }

    /** Build a MonitorContext with agentName for ThreadLocal propagation. */
    private static MonitorContext monCtx(AgentContext ctx, String agentName) {
        return MonitorContext.builder()
                .userId(String.valueOf(ctx.userId()))
                .appId(String.valueOf(ctx.appId()))
                .agentName(agentName)
                .build();
    }

    private List<String> extractTargetFiles(ReviewReport review) {
        if (review.getIssues() == null) return List.of();
        return review.getIssues().stream()
                .filter(i -> i.getFile() != null && !i.getFile().isBlank())
                .map(ReviewReport.Issue::getFile)
                .distinct()
                .toList();
    }

    private static String event(String type, String detail) {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("type", type);
        // Use null check only (not isBlank) so whitespace code tokens are preserved
        if (detail != null) map.put("detail", detail);
        return JSONUtil.toJsonStr(map);
    }

    private static String truncate(String s, int max) {
        if (s == null) return "";
        return s.length() <= max ? s : s.substring(0, max) + "...";
    }
}
