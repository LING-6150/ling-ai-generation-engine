package com.ling.lingaicodegeneration.multiagent.agent;

import com.ling.lingaicodegeneration.ai.multiagent.agent.AgentContext;
import com.ling.lingaicodegeneration.ai.multiagent.agent.CodeGenAgent;
import com.ling.lingaicodegeneration.ai.multiagent.model.CodeGenInput;
import com.ling.lingaicodegeneration.ai.multiagent.model.Complexity;
import com.ling.lingaicodegeneration.ai.multiagent.model.RequirementSpec;
import com.ling.lingaicodegeneration.ai.multiagent.model.TaskGraph;
import com.ling.lingaicodegeneration.core.AiCodeGeneratorFacade;
import com.ling.lingaicodegeneration.model.enums.CodeGenTypeEnum;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CodeGenAgentTest {

    @Mock
    private AiCodeGeneratorFacade aiCodeGeneratorFacade;

    @InjectMocks
    private CodeGenAgent codeGenAgent;

    private final AgentContext ctx = new AgentContext(1L, 1L, 3, "CodeGenAgent");
    private final TaskGraph taskGraph = new TaskGraph(true, 1, null, null);

    private CodeGenInput buildInput(CodeGenTypeEnum type) {
        RequirementSpec spec = RequirementSpec.builder()
                .codeGenType(type)
                .projectDescription("test project")
                .features(List.of())
                .techConstraints(List.of())
                .complexity(Complexity.SIMPLE)
                .build();
        return new CodeGenInput(spec, "", taskGraph);
    }

    @Test
    void execute_htmlType_delegatesToFacadeWithHtml() {
        Flux<String> mockFlux = Flux.just("token1", "token2");
        when(aiCodeGeneratorFacade.generateAndSaveCodeStream(anyString(),
                eq(CodeGenTypeEnum.HTML), eq(1L))).thenReturn(mockFlux);

        Flux<String> result = codeGenAgent.execute(buildInput(CodeGenTypeEnum.HTML), ctx);

        assertNotNull(result);
        verify(aiCodeGeneratorFacade).generateAndSaveCodeStream(
                anyString(), eq(CodeGenTypeEnum.HTML), eq(1L));
    }

    @Test
    void execute_multiFileType_delegatesToFacadeWithMultiFile() {
        Flux<String> mockFlux = Flux.just("css token");
        when(aiCodeGeneratorFacade.generateAndSaveCodeStream(anyString(),
                eq(CodeGenTypeEnum.MULTI_FILE), eq(1L))).thenReturn(mockFlux);

        Flux<String> result = codeGenAgent.execute(buildInput(CodeGenTypeEnum.MULTI_FILE), ctx);

        assertNotNull(result);
        verify(aiCodeGeneratorFacade).generateAndSaveCodeStream(
                anyString(), eq(CodeGenTypeEnum.MULTI_FILE), eq(1L));
    }

    @Test
    void execute_vueProjectType_delegatesToFacadeWithVue() {
        Flux<String> mockFlux = Flux.just("vue token");
        when(aiCodeGeneratorFacade.generateAndSaveCodeStream(anyString(),
                eq(CodeGenTypeEnum.VUE_PROJECT), eq(1L))).thenReturn(mockFlux);

        Flux<String> result = codeGenAgent.execute(buildInput(CodeGenTypeEnum.VUE_PROJECT), ctx);

        assertNotNull(result);
        verify(aiCodeGeneratorFacade).generateAndSaveCodeStream(
                anyString(), eq(CodeGenTypeEnum.VUE_PROJECT), eq(1L));
    }

    @Test
    void execute_promptIncludesProjectDescription() {
        RequirementSpec spec = RequirementSpec.builder()
                .codeGenType(CodeGenTypeEnum.HTML)
                .projectDescription("a beautiful portfolio site")
                .features(List.of())
                .techConstraints(List.of())
                .complexity(Complexity.SIMPLE)
                .build();
        TaskGraph graphWithHints = new TaskGraph(true, 1, "use Tailwind CSS", null);
        CodeGenInput input = new CodeGenInput(spec, "https://img.jpg", graphWithHints);

        when(aiCodeGeneratorFacade.generateAndSaveCodeStream(anyString(),
                any(), anyLong())).thenReturn(Flux.empty());

        codeGenAgent.execute(input, ctx);

        verify(aiCodeGeneratorFacade).generateAndSaveCodeStream(
                argThat(prompt ->
                        prompt.contains("a beautiful portfolio site") &&
                        prompt.contains("Tailwind CSS") &&
                        prompt.contains("https://img.jpg")),
                eq(CodeGenTypeEnum.HTML), eq(1L));
    }
}
