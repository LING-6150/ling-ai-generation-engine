package com.ling.lingaicodegeneration.multiagent.agent;

import com.ling.lingaicodegeneration.ai.multiagent.agent.AgentContext;
import com.ling.lingaicodegeneration.ai.multiagent.agent.RequirementAgent;
import com.ling.lingaicodegeneration.ai.multiagent.llm.RequirementLlmService;
import com.ling.lingaicodegeneration.ai.multiagent.model.Complexity;
import com.ling.lingaicodegeneration.ai.multiagent.model.RequirementSpec;
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
class RequirementAgentTest {

    @Mock
    private RequirementLlmService requirementLlmService;

    @InjectMocks
    private RequirementAgent requirementAgent;

    private final AgentContext ctx = new AgentContext(1L, 1L, 3, "RequirementAgent");

    @Test
    void execute_normalCase_returnsSpec() {
        RequirementSpec mockSpec = RequirementSpec.builder()
                .codeGenType(CodeGenTypeEnum.HTML)
                .projectDescription("A landing page")
                .features(List.of("hero section", "contact form"))
                .techConstraints(List.of())
                .complexity(Complexity.SIMPLE)
                .build();
        when(requirementLlmService.analyze(anyString())).thenReturn(mockSpec);

        RequirementSpec result = requirementAgent.execute("build me a landing page", ctx);

        assertEquals(CodeGenTypeEnum.HTML, result.getCodeGenType());
        assertEquals(Complexity.SIMPLE, result.getComplexity());
        assertEquals(2, result.getFeatures().size());
    }

    @Test
    void execute_nullCodeGenType_fallsBackToHtml() {
        RequirementSpec specWithNullType = RequirementSpec.builder()
                .projectDescription("something")
                .features(List.of())
                .techConstraints(List.of())
                .complexity(Complexity.MEDIUM)
                .build();
        when(requirementLlmService.analyze(anyString())).thenReturn(specWithNullType);

        RequirementSpec result = requirementAgent.execute("something", ctx);

        assertEquals(CodeGenTypeEnum.HTML, result.getCodeGenType());
    }

    @Test
    void execute_nullComplexity_defaultsToMedium() {
        RequirementSpec specNoComplexity = RequirementSpec.builder()
                .codeGenType(CodeGenTypeEnum.MULTI_FILE)
                .projectDescription("a site")
                .features(List.of())
                .techConstraints(List.of())
                .build();
        when(requirementLlmService.analyze(anyString())).thenReturn(specNoComplexity);

        RequirementSpec result = requirementAgent.execute("a site", ctx);

        assertEquals(Complexity.MEDIUM, result.getComplexity());
    }

    @Test
    void execute_llmThrowsException_returnsFallbackSpec() {
        when(requirementLlmService.analyze(anyString()))
                .thenThrow(new RuntimeException("API timeout"));

        RequirementSpec result = requirementAgent.execute("build a todo app", ctx);

        assertNotNull(result);
        assertEquals(CodeGenTypeEnum.HTML, result.getCodeGenType());
        assertEquals(Complexity.MEDIUM, result.getComplexity());
        assertNotNull(result.getFeatures());
    }
}
