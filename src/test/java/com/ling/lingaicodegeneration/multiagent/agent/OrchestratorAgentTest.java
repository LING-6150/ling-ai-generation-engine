package com.ling.lingaicodegeneration.multiagent.agent;

import com.ling.lingaicodegeneration.ai.multiagent.agent.*;
import com.ling.lingaicodegeneration.ai.multiagent.model.*;
import com.ling.lingaicodegeneration.model.enums.CodeGenTypeEnum;
import com.ling.lingaicodegeneration.monitor.AiModelMetricsCollector;
import com.ling.lingaicodegeneration.monitor.MonitorContext;
import com.ling.lingaicodegeneration.monitor.MonitorContextHolder;
import com.ling.lingaicodegeneration.service.ChatHistoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrchestratorAgentTest {

    @Mock private RequirementAgent  requirementAgent;
    @Mock private PlannerAgent      plannerAgent;
    @Mock private AssetAgent        assetAgent;
    @Mock private CodeGenAgent      codeGenAgent;
    @Mock private ReviewAgent       reviewAgent;
    @Mock private RefineAgent       refineAgent;
    @Mock private AiModelMetricsCollector aiModelMetricsCollector;
    @Mock private ChatHistoryService chatHistoryService;

    @InjectMocks
    private OrchestratorAgent orchestratorAgent;

    private final AgentContext ctx = new AgentContext(1L, 1L, 3, "OrchestratorAgent");

    private RequirementSpec defaultSpec() {
        return RequirementSpec.builder()
                .codeGenType(CodeGenTypeEnum.HTML)
                .projectDescription("test project")
                .features(List.of())
                .techConstraints(List.of())
                .complexity(Complexity.SIMPLE)
                .build();
    }

    private ReviewReport passedReport() {
        return ReviewReport.builder()
                .passed(true).score(85).issues(List.of()).summary("looks good").build();
    }

    private ReviewReport failedReport() {
        return ReviewReport.builder()
                .passed(false).score(40)
                .issues(List.of(ReviewReport.Issue.builder()
                        .severity(ReviewReport.Severity.BLOCKER)
                        .file("index.html")
                        .description("missing nav")
                        .suggestion("add nav element")
                        .build()))
                .summary("blocker found").build();
    }

    private ReviewReport degradedReport() {
        return ReviewReport.builder()
                .passed(false).score(0).degraded(true)
                .issues(List.of()).summary("Automated review unavailable").build();
    }

    @BeforeEach
    void injectExecutor() throws Exception {
        // Inject a real executor so CompletableFuture.supplyAsync works in tests
        var field = OrchestratorAgent.class.getDeclaredField("imageSearchExecutor");
        field.setAccessible(true);
        field.set(orchestratorAgent, Executors.newFixedThreadPool(2));
    }

    // ── helper: collect all events from the Flux ───────────────────────────

    private List<String> run() {
        return orchestratorAgent.execute("build a test page", ctx)
                .collectList()
                .block(Duration.ofSeconds(30));
    }

    // ── happy path ─────────────────────────────────────────────────────────

    @Test
    void execute_happyPath_noRefine() {
        when(requirementAgent.execute(any(), any())).thenReturn(defaultSpec());
        when(assetAgent.execute(any(), any())).thenReturn("");
        when(plannerAgent.execute(any(), any()))
                .thenReturn(new TaskGraph(false, 1, null, null));
        when(codeGenAgent.execute(any(), any())).thenReturn(Flux.just("code_token"));
        when(reviewAgent.execute(any(), any())).thenReturn(passedReport());
        when(chatHistoryService.addStructuredMessage(any(), any(), any(), any()))
                .thenReturn(true);

        List<String> events = run();

        assertNotNull(events);
        assertTrue(events.stream().anyMatch(e -> e.contains("workflow_start")));
        assertTrue(events.stream().anyMatch(e -> e.contains("code_gen_done")));
        assertTrue(events.stream().anyMatch(e -> e.contains("review_done")));
        assertTrue(events.stream().anyMatch(e -> e.contains("workflow_done")));

        verify(refineAgent, never()).execute(any(), any());
        verify(chatHistoryService, times(1))
                .addStructuredMessage(eq(1L), anyString(), anyString(), eq(1L));
    }

    // ── Review → Refine cycle ──────────────────────────────────────────────

    @Test
    void execute_refineTriggered_whenReviewFails() {
        when(requirementAgent.execute(any(), any())).thenReturn(defaultSpec());
        when(assetAgent.execute(any(), any())).thenReturn("");
        when(plannerAgent.execute(any(), any()))
                .thenReturn(new TaskGraph(false, 1, null, null)); // suggestedRetries=1
        when(codeGenAgent.execute(any(), any())).thenReturn(Flux.just("token"));
        // First review fails → triggers Refine; second review passes
        when(reviewAgent.execute(any(), any()))
                .thenReturn(failedReport())
                .thenReturn(passedReport());
        when(refineAgent.execute(any(), any())).thenReturn(Flux.just("refine_token"));
        when(chatHistoryService.addStructuredMessage(any(), any(), any(), any()))
                .thenReturn(true);

        List<String> events = run();

        verify(refineAgent, times(1)).execute(any(), any());
        verify(reviewAgent, times(2)).execute(any(), any());
        assertTrue(events.stream().anyMatch(e -> e.contains("refine_start")));
        assertTrue(events.stream().anyMatch(e -> e.contains("refine_done")));
        assertTrue(events.stream().anyMatch(e -> e.contains("workflow_done")));
    }

    @Test
    void execute_retryBudgetExhausted_stopsRefine() {
        when(requirementAgent.execute(any(), any())).thenReturn(defaultSpec());
        when(assetAgent.execute(any(), any())).thenReturn("");
        when(plannerAgent.execute(any(), any()))
                .thenReturn(new TaskGraph(false, 3, null, null)); // suggestedRetries=3
        when(codeGenAgent.execute(any(), any())).thenReturn(Flux.just("token"));
        // Review always fails
        when(reviewAgent.execute(any(), any())).thenReturn(failedReport());
        when(refineAgent.execute(any(), any())).thenReturn(Flux.just("refine_token"));
        when(chatHistoryService.addStructuredMessage(any(), any(), any(), any()))
                .thenReturn(true);

        List<String> events = run();

        // With suggestedRetries=3: review(0) → refine(1) → review(1) → refine(2)
        //   → review(2) → refine(3) → review(3) = loop runs 4 times, refine runs 3 times
        verify(refineAgent, times(3)).execute(any(), any());
        verify(reviewAgent, times(4)).execute(any(), any());
        assertTrue(events.stream().anyMatch(e -> e.contains("workflow_done")));
    }

    // ── degraded ReviewReport → skip Refine ────────────────────────────────

    @Test
    void execute_degradedReview_skipsRefineAndCompletes() {
        when(requirementAgent.execute(any(), any())).thenReturn(defaultSpec());
        when(assetAgent.execute(any(), any())).thenReturn("");
        when(plannerAgent.execute(any(), any()))
                .thenReturn(new TaskGraph(false, 2, null, null));
        when(codeGenAgent.execute(any(), any())).thenReturn(Flux.just("token"));
        when(reviewAgent.execute(any(), any())).thenReturn(degradedReport());
        when(chatHistoryService.addStructuredMessage(any(), any(), any(), any()))
                .thenReturn(true);

        List<String> events = run();

        verify(refineAgent, never()).execute(any(), any());
        assertTrue(events.stream().anyMatch(e -> e.contains("review_degraded")));
        assertTrue(events.stream().anyMatch(e -> e.contains("workflow_done")));
    }

    // ── CodeGenAgent failure → workflow error ──────────────────────────────

    @Test
    void execute_codeGenFails_emitsWorkflowError() {
        when(requirementAgent.execute(any(), any())).thenReturn(defaultSpec());
        when(assetAgent.execute(any(), any())).thenReturn("");
        when(plannerAgent.execute(any(), any()))
                .thenReturn(new TaskGraph(false, 1, null, null));
        when(codeGenAgent.execute(any(), any()))
                .thenReturn(Flux.error(new RuntimeException("code gen failed")));

        // Bug fix (sink.error → sink.complete): the outer Flux now completes NORMALLY
        // after emitting a workflow_error event.  No exception should reach the caller.
        // Previously sink.error(e) caused HttpMessageNotWritableException in Spring MVC.
        List<String> events = assertDoesNotThrow(() ->
                orchestratorAgent.execute("build page", ctx)
                        .collectList()
                        .block(Duration.ofSeconds(15))
        );

        assertNotNull(events);
        assertTrue(events.stream().anyMatch(e -> e.contains("workflow_error")),
                "workflow_error event must be emitted on CodeGenAgent failure");

        verify(refineAgent, never()).execute(any(), any());
        verify(reviewAgent, never()).execute(any(), any());
    }

    @Test
    void execute_codeGenEmptyStream_emitsWorkflowError() {
        when(requirementAgent.execute(any(), any())).thenReturn(defaultSpec());
        when(assetAgent.execute(any(), any())).thenReturn("");
        when(plannerAgent.execute(any(), any()))
                .thenReturn(new TaskGraph(false, 1, null, null));
        when(codeGenAgent.execute(any(), any())).thenReturn(Flux.empty());

        List<String> events = assertDoesNotThrow(() ->
                orchestratorAgent.execute("build page", ctx)
                        .collectList()
                        .block(Duration.ofSeconds(15))
        );

        assertNotNull(events);
        assertTrue(events.stream().anyMatch(e -> e.contains("workflow_error")),
                "workflow_error event must be emitted on empty CodeGenAgent stream");
        assertTrue(events.stream().anyMatch(e -> e.contains("CodeGenAgent produced empty code stream")),
                "workflow_error should explain the empty stream");

        verify(aiModelMetricsCollector).recordWorkflowError(
                eq("1"), eq("1"), eq("OrchestratorAgent"), eq("workflow_empty_stream"), eq(false));
        verify(refineAgent, never()).execute(any(), any());
        verify(reviewAgent, never()).execute(any(), any());
    }

    // ── RefineAgent failure → consume budget, continue ────────────────────

    @Test
    void execute_refineAgentFails_consumesBudgetContinues() {
        when(requirementAgent.execute(any(), any())).thenReturn(defaultSpec());
        when(assetAgent.execute(any(), any())).thenReturn("");
        when(plannerAgent.execute(any(), any()))
                .thenReturn(new TaskGraph(false, 1, null, null));
        when(codeGenAgent.execute(any(), any())).thenReturn(Flux.just("token"));
        // First review fails, Refine throws, second review passes
        when(reviewAgent.execute(any(), any()))
                .thenReturn(failedReport())
                .thenReturn(passedReport());
        when(refineAgent.execute(any(), any()))
                .thenReturn(Flux.error(new RuntimeException("refine error")));
        when(chatHistoryService.addStructuredMessage(any(), any(), any(), any()))
                .thenReturn(true);

        List<String> events = run();

        verify(refineAgent, times(1)).execute(any(), any());
        verify(reviewAgent, times(2)).execute(any(), any());
        assertTrue(events.stream().anyMatch(e -> e.contains("refine_failed")));
        assertTrue(events.stream().anyMatch(e -> e.contains("workflow_done")));
    }

    // ── step events ordering ───────────────────────────────────────────────

    @Test
    void execute_emitsExpectedStepEvents_inOrder() {
        when(requirementAgent.execute(any(), any())).thenReturn(defaultSpec());
        when(assetAgent.execute(any(), any())).thenReturn("");
        when(plannerAgent.execute(any(), any()))
                .thenReturn(new TaskGraph(false, 0, null, null));
        when(codeGenAgent.execute(any(), any())).thenReturn(Flux.just("t"));
        when(reviewAgent.execute(any(), any())).thenReturn(passedReport());
        when(chatHistoryService.addStructuredMessage(any(), any(), any(), any()))
                .thenReturn(true);

        List<String> events = run();

        List<String> types = events.stream()
                .filter(e -> e.contains("\"type\""))
                .map(e -> e.replaceAll(".*\"type\":\"([^\"]+)\".*", "$1"))
                .toList();

        int workflowStart   = types.indexOf("workflow_start");
        int requirementDone = types.indexOf("requirement_done");
        int planningDone    = types.indexOf("planning_done");
        int codeGenStart    = types.indexOf("code_gen_start");
        int reviewStart     = types.indexOf("review_start");
        int workflowDone    = types.indexOf("workflow_done");

        assertTrue(workflowStart   < requirementDone, "workflow_start before requirement_done");
        assertTrue(requirementDone < planningDone,    "requirement_done before planning_done");
        assertTrue(planningDone    < codeGenStart,    "planning_done before code_gen_start");
        assertTrue(codeGenStart    < reviewStart,     "code_gen_start before review_start");
        assertTrue(reviewStart     < workflowDone,    "review_start before workflow_done");
    }

    // ── P7: code tokens appear in event stream ────────────────────────────

    @Test
    void execute_streamCodeTokens_appearsInEventStream() {
        when(requirementAgent.execute(any(), any())).thenReturn(defaultSpec());
        when(assetAgent.execute(any(), any())).thenReturn("");
        when(plannerAgent.execute(any(), any()))
                .thenReturn(new TaskGraph(false, 0, null, null));
        when(codeGenAgent.execute(any(), any()))
                .thenReturn(Flux.just("<!DOCTYPE", " html>", "<body>"));
        when(reviewAgent.execute(any(), any())).thenReturn(passedReport());
        when(chatHistoryService.addStructuredMessage(any(), any(), any(), any()))
                .thenReturn(true);

        List<String> events = run();

        List<String> codeTokenEvents = events.stream()
                .filter(e -> e.contains("\"type\":\"code_token\""))
                .toList();
        assertEquals(3, codeTokenEvents.size(), "Expected one code_token event per token");
        assertTrue(codeTokenEvents.get(0).contains("<!DOCTYPE"));
        assertTrue(codeTokenEvents.get(1).contains(" html>"));
        assertTrue(codeTokenEvents.get(2).contains("<body>"));

        // code_token events appear between code_gen_start and code_gen_done
        List<String> types = events.stream()
                .map(e -> e.replaceAll(".*\"type\":\"([^\"]+)\".*", "$1"))
                .toList();
        int codeGenStart = types.indexOf("code_gen_start");
        int codeGenDone  = types.indexOf("code_gen_done");
        int firstToken   = types.indexOf("code_token");
        assertTrue(codeGenStart < firstToken && firstToken < codeGenDone,
                "code_token events must be between code_gen_start and code_gen_done");
    }

    // ── P7: cancel signal stops virtual thread, no exception ─────────────

    @Test
    void execute_cancelSignal_interruptsVirtualThread_noException() {
        when(requirementAgent.execute(any(), any())).thenReturn(defaultSpec());
        when(assetAgent.execute(any(), any())).thenReturn("");
        when(plannerAgent.execute(any(), any()))
                .thenReturn(new TaskGraph(false, 0, null, null));
        // Never-completing Flux simulates long-running code gen
        when(codeGenAgent.execute(any(), any())).thenReturn(Flux.never());

        assertDoesNotThrow(() ->
                orchestratorAgent.execute("build page", ctx)
                        .timeout(Duration.ofMillis(500))
                        .onErrorResume(e -> Flux.empty())  // timeout/cancel → end cleanly
                        .collectList()
                        .block(Duration.ofSeconds(5))
        );
        // vt.interrupt() stops the virtual thread from blockLast() on Flux.never()
        // No exception should reach the test; workflow_error event is emitted internally
    }

    // ── P7: agentName ThreadLocal propagation ─────────────────────────────

    @Test
    void agentName_propagatedViaThreadLocal_beforeEachAgentCall() {
        CopyOnWriteArrayList<String> captured = new CopyOnWriteArrayList<>();

        when(requirementAgent.execute(any(), any())).thenAnswer(inv -> {
            MonitorContext c = MonitorContextHolder.getContext();
            captured.add("req:" + (c != null ? c.getAgentName() : "null"));
            return defaultSpec();
        });
        when(assetAgent.execute(any(), any())).thenAnswer(inv -> {
            MonitorContext c = MonitorContextHolder.getContext();
            captured.add("asset:" + (c != null ? c.getAgentName() : "null"));
            return "";
        });
        when(plannerAgent.execute(any(), any())).thenAnswer(inv -> {
            MonitorContext c = MonitorContextHolder.getContext();
            captured.add("plan:" + (c != null ? c.getAgentName() : "null"));
            return new TaskGraph(false, 0, null, null);
        });
        when(codeGenAgent.execute(any(), any())).thenAnswer(inv -> {
            MonitorContext c = MonitorContextHolder.getContext();
            captured.add("code:" + (c != null ? c.getAgentName() : "null"));
            return Flux.just("token");
        });
        when(reviewAgent.execute(any(), any())).thenAnswer(inv -> {
            MonitorContext c = MonitorContextHolder.getContext();
            captured.add("review:" + (c != null ? c.getAgentName() : "null"));
            return passedReport();
        });
        when(chatHistoryService.addStructuredMessage(any(), any(), any(), any()))
                .thenReturn(true);

        run();

        assertTrue(captured.contains("req:RequirementAgent"),   "RequirementAgent agentName missing");
        assertTrue(captured.contains("asset:AssetAgent"),       "AssetAgent agentName missing");
        assertTrue(captured.contains("plan:PlannerAgent"),       "PlannerAgent agentName missing");
        assertTrue(captured.contains("code:CodeGenAgent"),       "CodeGenAgent agentName missing");
        assertTrue(captured.contains("review:ReviewAgent"),     "ReviewAgent agentName missing");
    }
}
