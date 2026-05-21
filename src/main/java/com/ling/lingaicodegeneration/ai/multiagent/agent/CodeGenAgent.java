package com.ling.lingaicodegeneration.ai.multiagent.agent;

import com.ling.lingaicodegeneration.ai.multiagent.model.CodeGenInput;
import com.ling.lingaicodegeneration.core.AiCodeGeneratorFacade;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Slf4j
@Component
public class CodeGenAgent implements Agent<CodeGenInput, Flux<String>> {

    @Resource
    private AiCodeGeneratorFacade aiCodeGeneratorFacade;

    @Override
    public Flux<String> execute(CodeGenInput input, AgentContext ctx) {
        log.info("CodeGenAgent executing, appId: {}, codeGenType: {}",
                ctx.appId(), input.requirementSpec().getCodeGenType());
        return aiCodeGeneratorFacade.generateAndSaveCodeStream(
                input.buildPrompt(),
                input.requirementSpec().getCodeGenType(),
                ctx.appId()
        );
    }
}
