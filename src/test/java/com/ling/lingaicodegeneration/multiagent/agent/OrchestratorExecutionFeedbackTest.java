package com.ling.lingaicodegeneration.multiagent.agent;

import com.ling.lingaicodegeneration.ai.multiagent.agent.*;
import com.ling.lingaicodegeneration.ai.multiagent.model.*;
import com.ling.lingaicodegeneration.ai.multiagent.verify.ExecutionVerifier;
import com.ling.lingaicodegeneration.ai.multiagent.verify.VerificationReport;
import com.ling.lingaicodegeneration.model.enums.CodeGenTypeEnum;
import com.ling.lingaicodegeneration.monitor.AiModelMetricsCollector;
import com.ling.lingaicodegeneration.service.ChatHistoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrchestratorExecutionFeedbackTest {

    @Mock private RequirementAgent requirementAgent;
    @Mock private PlannerAgent plannerAgent;
    @Mock private AssetAgent assetAgent;
    @Mock private CodeGenAgent codeGenAgent;
    @Mock private ReviewAgent reviewAgent;
    @Mock private RefineAgent refineAgent;
    @Mock private ExecutionVerifier executionVerifier;
    @Mock private AiModelMetricsCollector aiModelMetricsCollector;
    @Mock private ChatHistoryService chatHistoryService;

    @InjectMocks
    private OrchestratorAgent orchestratorAgent;

    private static final VerificationReport PAGE_ERROR = VerificationReport.failed(
            List.of(new VerificationReport.Failure("page_error", "startTimer is not defined")), 30);

    @BeforeEach
    void setUp() throws Exception {
        var field = OrchestratorAgent.class.getDeclaredField("imageSearchExecutor");
        field.setAccessible(true);
        field.set(orchestratorAgent, Executors.newFixedThreadPool(2));

        when(requirementAgent.execute(any(), any())).thenReturn(RequirementSpec.builder()
                .codeGenType(CodeGenTypeEnum.HTML)
                .projectDescription("timer")
                .features(List.of())
                .techConstraints(List.of())
                .complexity(Complexity.SIMPLE)
                .build());
        when(assetAgent.execute(any(), any())).thenReturn("");
        when(codeGenAgent.execute(any(), any())).thenReturn(Flux.just("code"));
        lenient().when(chatHistoryService.addStructuredMessage(any(), any(), any(), any())).thenReturn(true);
    }

    private void plannerRetries(int retries) {
        when(plannerAgent.execute(any(), any())).thenReturn(new TaskGraph(false, retries, null, null));
    }

    private static ReviewReport passedReview() {
        return ReviewReport.builder().passed(true).score(90).issues(List.of()).summary("ok").build();
    }

    private List<String> run(boolean executionFeedback) {
        AgentContext ctx = new AgentContext(1L, 1L, 3, "OrchestratorAgent", false, executionFeedback);
        return orchestratorAgent.execute("a countdown timer", ctx)
                .collectList()
                .block(Duration.ofSeconds(30));
    }

    @Test
    void flagOff_verifierNeverRuns_andNoVerifyEvents() {
        plannerRetries(1);
        when(reviewAgent.execute(any(), any())).thenReturn(passedReview());

        List<String> events = run(false);

        verifyNoInteractions(executionVerifier);
        verify(refineAgent, never()).execute(any(), any());
        assertTrue(events.stream().noneMatch(e -> e.contains("verify_")));
        assertTrue(events.stream().anyMatch(e -> e.contains("workflow_done")));
    }

    @Test
    void reviewPasses_butExecutionFails_triggersRefineWithFeedback() {
        plannerRetries(1);
        when(reviewAgent.execute(any(), any())).thenReturn(passedReview());
        when(executionVerifier.verify(eq(CodeGenTypeEnum.HTML), anyString()))
                .thenReturn(PAGE_ERROR)
                .thenReturn(VerificationReport.passed(25));
        when(refineAgent.execute(any(), any())).thenReturn(Flux.just("fixed"));

        List<String> events = run(true);

        ArgumentCaptor<RefineInput> captor = ArgumentCaptor.forClass(RefineInput.class);
        verify(refineAgent, times(1)).execute(captor.capture(), any());
        assertSame(PAGE_ERROR, captor.getValue().verificationReport());
        verify(reviewAgent, times(2)).execute(any(), any());
        assertTrue(events.stream().anyMatch(e -> e.contains("verify_done") && e.contains("passed=false")));
        assertTrue(events.stream().anyMatch(e -> e.contains("verify_done") && e.contains("passed=true")));
    }

    @Test
    void verifierUnavailable_fallsBackToReviewOnly() {
        plannerRetries(1);
        when(reviewAgent.execute(any(), any())).thenReturn(passedReview());
        when(executionVerifier.verify(any(), anyString()))
                .thenReturn(VerificationReport.unavailable("Executable doesn't exist", 5));

        List<String> events = run(true);

        verify(refineAgent, never()).execute(any(), any());
        assertTrue(events.stream().anyMatch(e -> e.contains("verify_done") && e.contains("unavailable")));
        assertTrue(events.stream().anyMatch(e -> e.contains("workflow_done")));
    }

    @Test
    void executionFailsOnLastAttempt_stopsWithoutExtraRefine() {
        plannerRetries(0);
        when(reviewAgent.execute(any(), any())).thenReturn(passedReview());
        when(executionVerifier.verify(any(), anyString())).thenReturn(PAGE_ERROR);

        List<String> events = run(true);

        verify(refineAgent, never()).execute(any(), any());
        verify(executionVerifier, times(1)).verify(any(), anyString());
        assertTrue(events.stream().anyMatch(e -> e.contains("workflow_done")));
    }

    @Test
    void reviewFails_executionPasses_refineGetsNoFeedback() {
        plannerRetries(1);
        ReviewReport failed = ReviewReport.builder().passed(false).score(40)
                .issues(List.of(ReviewReport.Issue.builder()
                        .severity(ReviewReport.Severity.MAJOR).file("index.html")
                        .description("no reset button").suggestion("add one").build()))
                .summary("missing feature").build();
        when(reviewAgent.execute(any(), any())).thenReturn(failed).thenReturn(passedReview());
        when(executionVerifier.verify(any(), anyString())).thenReturn(VerificationReport.passed(20));
        when(refineAgent.execute(any(), any())).thenReturn(Flux.just("fixed"));

        run(true);

        ArgumentCaptor<RefineInput> captor = ArgumentCaptor.forClass(RefineInput.class);
        verify(refineAgent).execute(captor.capture(), any());
        assertNull(captor.getValue().verificationReport());
    }
}
