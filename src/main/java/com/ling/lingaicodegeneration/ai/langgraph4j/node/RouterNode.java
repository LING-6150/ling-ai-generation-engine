package com.ling.lingaicodegeneration.ai.langgraph4j.node;

import com.ling.lingaicodegeneration.ai.AiCodeGenTypeRoutingService;
import com.ling.lingaicodegeneration.ai.langgraph4j.state.WorkflowContext;
import com.ling.lingaicodegeneration.model.enums.CodeGenTypeEnum;
import com.ling.lingaicodegeneration.utils.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.bsc.langgraph4j.action.AsyncNodeAction;
import org.bsc.langgraph4j.state.AgentState;

import static org.bsc.langgraph4j.action.AsyncNodeAction.node_async;

@Slf4j
public class RouterNode {

    public static AsyncNodeAction<AgentState> create() {
        return node_async(state -> {
            WorkflowContext context = WorkflowContext.getContext(state);
            log.info("Executing node: Smart Router");
            CodeGenTypeEnum generationType;
            try {
                AiCodeGenTypeRoutingService routingService =
                        SpringContextUtil.getBean(AiCodeGenTypeRoutingService.class);
                generationType = routingService.routeCodeGenType(
                        context.getOriginalPrompt());
                if (generationType == null) {
                    generationType = CodeGenTypeEnum.MULTI_FILE;
                }
                log.info("AI routing completed, selected type: {}", generationType.getValue());
            } catch (Exception e) {
                log.error("AI routing failed, fallback to MULTI_FILE: {}", e.getMessage());
                generationType = CodeGenTypeEnum.MULTI_FILE;
            }
            context.setCurrentStep("Smart Router");
            context.setGenerationType(generationType);
            return WorkflowContext.saveContext(context);
        });
    }
}