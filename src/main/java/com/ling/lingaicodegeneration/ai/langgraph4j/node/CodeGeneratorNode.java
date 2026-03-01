package com.ling.lingaicodegeneration.ai.langgraph4j.node;

import com.ling.lingaicodegeneration.ai.langgraph4j.state.WorkflowContext;
import com.ling.lingaicodegeneration.constant.AppConstant;
import com.ling.lingaicodegeneration.core.AiCodeGeneratorFacade;
import com.ling.lingaicodegeneration.model.enums.CodeGenTypeEnum;
import com.ling.lingaicodegeneration.utils.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.bsc.langgraph4j.action.AsyncNodeAction;
import org.bsc.langgraph4j.state.AgentState;
import reactor.core.publisher.Flux;

import java.time.Duration;

import static org.bsc.langgraph4j.action.AsyncNodeAction.node_async;

@Slf4j
public class CodeGeneratorNode {

    public static AsyncNodeAction<AgentState> create() {
        return node_async(state -> {
            WorkflowContext context = WorkflowContext.getContext(state);
            log.info("Executing node: Code Generation");
            String userMessage = context.getEnhancedPrompt();
            CodeGenTypeEnum generationType = context.getGenerationType();
            Long appId = context.getAppId();
            log.info("Generating code, type: {}, appId: {}",
                    generationType.getValue(), appId);
            AiCodeGeneratorFacade codeGeneratorFacade =
                    SpringContextUtil.getBean(AiCodeGeneratorFacade.class);
            // Use blocking call - wait for stream to complete
            Flux<String> codeStream = codeGeneratorFacade
                    .generateAndSaveCodeStream(userMessage, generationType, appId);
            codeStream.blockLast(Duration.ofMinutes(10));
            // Build generated code directory path
            String generatedCodeDir = String.format("%s/%s_%s",
                    AppConstant.CODE_OUTPUT_ROOT_DIR,
                    generationType.getValue(), appId);
            log.info("Code generation completed, dir: {}", generatedCodeDir);
            context.setCurrentStep("Code Generation");
            context.setGeneratedCodeDir(generatedCodeDir);
            return WorkflowContext.saveContext(context);
        });
    }
}