package com.ling.lingaicodegeneration.ai.multiagent.agent;

import cn.hutool.json.JSONUtil;
import com.ling.lingaicodegeneration.ai.multiagent.model.*;
import com.ling.lingaicodegeneration.core.CodeFileSaver;
import com.ling.lingaicodegeneration.model.enums.ChatHistoryMessageTypeEnum;
import com.ling.lingaicodegeneration.model.enums.CodeGenTypeEnum;
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
        return Flux.create(sink -> Thread.startVirtualThread(() -> {
            log.info("OrchestratorAgent starting, appId: {}", ctx.appId());
            ReviewReport finalReview = null;
            try {
                sink.next(event("workflow_start", null));

                // ── 1. RequirementAgent + AssetAgent in parallel ─────────────
                // Both depend only on originalPrompt; run concurrently.
                // Each future catches internally so allOf() never fails.
                CompletableFuture<RequirementSpec> reqFuture = CompletableFuture.supplyAsync(() -> {
                    try {
                        return requirementAgent.execute(message,
                                ctx.withAgentName("RequirementAgent"));
                    } catch (Exception e) {
                        log.warn("RequirementAgent failed, using fallback: {}", e.getMessage());
                        return RequirementSpec.builder()
                                .codeGenType(CodeGenTypeEnum.HTML)
                                .projectDescription(message)
                                .features(List.of())
                                .techConstraints(List.of())
                                .complexity(Complexity.MEDIUM)
                                .build();
                    }
                }, imageSearchExecutor);

                CompletableFuture<String> assetFuture = CompletableFuture.supplyAsync(() -> {
                    try {
                        return assetAgent.execute(message, ctx.withAgentName("AssetAgent"));
                    } catch (Exception e) {
                        log.warn("AssetAgent failed, returning empty images: {}", e.getMessage());
                        return "";
                    }
                }, imageSearchExecutor);

                CompletableFuture.allOf(reqFuture, assetFuture).join();
                RequirementSpec spec   = reqFuture.join();
                String          images = assetFuture.join();

                sink.next(event("requirement_done", "type=" + spec.getCodeGenType()));
                sink.next(event("asset_done",
                        images.isBlank() ? "no-images" : "images-found"));

                // ── 2. PlannerAgent ───────────────────────────────────────────
                sink.next(event("planning_start", null));
                TaskGraph plan = plannerAgent.execute(spec, ctx.withAgentName("PlannerAgent"));
                sink.next(event("planning_done", "retries=" + plan.suggestedRetries()));

                // ── 3. CodeGenAgent ───────────────────────────────────────────
                sink.next(event("code_gen_start", null));
                String outputDir = CodeFileSaver.resolveOutputDir(spec.getCodeGenType(), ctx.appId());
                CodeGenInput codeInput = new CodeGenInput(spec, images, plan);
                Flux<String> codeFlux = codeGenAgent.execute(codeInput,
                        ctx.withAgentName("CodeGenAgent"));

                // 临时实现：P6 阶段阻塞等待 CodeGenAgent 完成后才进入 Review。
                // P7 阶段将升级为真正的流式透传，参考重构计划 Phase 7。
                // 当前限制：用户看到步骤事件但看不到代码 token 流。
                codeFlux.blockLast(Duration.ofMinutes(5));
                sink.next(event("code_gen_done", null));

                // ── 4. Review → Refine loop ───────────────────────────────────
                int maxRetries = plan.suggestedRetries();
                for (int attempt = 0; attempt <= maxRetries; attempt++) {
                    sink.next(event("review_start",
                            "attempt=" + (attempt + 1) + "/" + (maxRetries + 1)));
                    ReviewInput reviewInput = new ReviewInput(outputDir, spec);
                    finalReview = reviewAgent.execute(reviewInput,
                            ctx.withAgentName("ReviewAgent"));

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

                    // Refine
                    sink.next(event("refine_start", "attempt=" + (attempt + 1)));
                    List<String> targetFiles = extractTargetFiles(finalReview);
                    RefinementPlan refinePlan = new RefinementPlan(
                            targetFiles, plan.reviewFocusAreas(), attempt + 1);
                    RefineInput refineInput = new RefineInput(outputDir, finalReview, refinePlan);

                    try {
                        Flux<String> refineFlux = refineAgent.execute(refineInput,
                                ctx.withAgentName("RefineAgent"));
                        // Same P6 temporary blocking pattern as CodeGenAgent above.
                        refineFlux.blockLast(Duration.ofMinutes(5));
                        sink.next(event("refine_done", "attempt=" + (attempt + 1)));
                    } catch (Exception e) {
                        // RefineAgent failure: consume retry budget and continue to next Review.
                        log.warn("RefineAgent attempt {} failed, continuing to next review: {}",
                                attempt + 1, e.getMessage());
                        sink.next(event("refine_failed", "attempt=" + (attempt + 1)));
                    }
                }

                // ── 5. Persist final ReviewReport to chat_history ─────────────
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
                log.error("OrchestratorAgent fatal error, appId: {}: {}", ctx.appId(),
                        e.getMessage(), e);
                sink.next(event("workflow_error", truncate(e.getMessage(), 200)));
                sink.error(e);
            }
        }));
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
        if (detail != null && !detail.isBlank()) map.put("detail", detail);
        return JSONUtil.toJsonStr(map);
    }

    private static String truncate(String s, int max) {
        if (s == null) return "";
        return s.length() <= max ? s : s.substring(0, max) + "...";
    }
}
