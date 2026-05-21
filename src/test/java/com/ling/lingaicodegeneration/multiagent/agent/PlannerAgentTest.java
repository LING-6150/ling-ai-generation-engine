package com.ling.lingaicodegeneration.multiagent.agent;

import com.ling.lingaicodegeneration.ai.multiagent.agent.AgentContext;
import com.ling.lingaicodegeneration.ai.multiagent.agent.PlannerAgent;
import com.ling.lingaicodegeneration.ai.multiagent.llm.PlannerLlmService;
import com.ling.lingaicodegeneration.ai.multiagent.model.Complexity;
import com.ling.lingaicodegeneration.ai.multiagent.model.RequirementSpec;
import com.ling.lingaicodegeneration.ai.multiagent.model.TaskGraph;
import com.ling.lingaicodegeneration.model.enums.CodeGenTypeEnum;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PlannerAgentTest {

    @Mock
    private PlannerLlmService plannerLlmService;

    @InjectMocks
    private PlannerAgent plannerAgent;

    private final AgentContext ctx = new AgentContext(1L, 1L, 3, "PlannerAgent");

    private RequirementSpec buildSpec(Complexity complexity) {
        return RequirementSpec.builder()
                .codeGenType(CodeGenTypeEnum.HTML)
                .projectDescription("test project")
                .features(List.of())
                .techConstraints(List.of())
                .complexity(complexity)
                .build();
    }

    @Test
    void execute_normalCase_returnsTaskGraph() {
        when(plannerLlmService.plan(anyString()))
                .thenReturn(new TaskGraph(true, 2, "use Tailwind", "check responsiveness"));

        TaskGraph result = plannerAgent.execute(buildSpec(Complexity.MEDIUM), ctx);

        assertTrue(result.parallelizeAsset());
        assertEquals(2, result.suggestedRetries());
        assertEquals("use Tailwind", result.codeGenHints());
    }

    @Test
    void execute_retriesClampedTo3() {
        when(plannerLlmService.plan(anyString()))
                .thenReturn(new TaskGraph(true, 99, null, null));

        TaskGraph result = plannerAgent.execute(buildSpec(Complexity.COMPLEX), ctx);

        assertEquals(3, result.suggestedRetries());
    }

    @Test
    void execute_negativeSuggestedRetries_clampedTo0() {
        when(plannerLlmService.plan(anyString()))
                .thenReturn(new TaskGraph(false, -1, null, null));

        TaskGraph result = plannerAgent.execute(buildSpec(Complexity.SIMPLE), ctx);

        assertEquals(0, result.suggestedRetries());
    }

    @Test
    void execute_llmThrowsException_usesComplexityBasedDefaults() {
        when(plannerLlmService.plan(anyString()))
                .thenThrow(new RuntimeException("network error"));

        TaskGraph simpleResult = plannerAgent.execute(buildSpec(Complexity.SIMPLE), ctx);
        assertEquals(1, simpleResult.suggestedRetries());

        TaskGraph complexResult = plannerAgent.execute(buildSpec(Complexity.COMPLEX), ctx);
        assertEquals(3, complexResult.suggestedRetries());

        assertTrue(simpleResult.parallelizeAsset());
    }
}
