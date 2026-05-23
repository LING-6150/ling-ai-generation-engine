package com.ling.lingaicodegeneration.multiagent.agent;

import com.ling.lingaicodegeneration.ai.multiagent.agent.*;
import com.ling.lingaicodegeneration.ai.multiagent.model.*;
import com.ling.lingaicodegeneration.model.enums.CodeGenTypeEnum;
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

        // Flux ends in error; we expect a RuntimeException propagated
        assertThrows(RuntimeException.class, () ->
                orchestratorAgent.execute("build page", ctx)
                        .collectList()
                        .block(Duration.ofSeconds(15))
        );

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
}
