package com.ling.lingaicodegeneration.ai.langgraph4j.node;

import com.ling.lingaicodegeneration.ai.langgraph4j.state.WorkflowContext;
import lombok.extern.slf4j.Slf4j;
import org.bsc.langgraph4j.action.AsyncNodeAction;
import org.bsc.langgraph4j.state.AgentState;

import static org.bsc.langgraph4j.action.AsyncNodeAction.node_async;

@Slf4j
public class PromptEnhancerNode {

    public static AsyncNodeAction<AgentState> create() {
        return node_async(state -> {
            WorkflowContext context = WorkflowContext.getContext(state);
            log.info("Executing node: Prompt Enhancement");
            String originalPrompt = context.getOriginalPrompt();
            String imageListStr = context.getImageListStr();
            // Build enhanced prompt
            StringBuilder enhancedPromptBuilder = new StringBuilder();
            enhancedPromptBuilder.append(originalPrompt);
            // If images were collected, append image info
            if (imageListStr != null && !imageListStr.isBlank()) {
                enhancedPromptBuilder.append("\n\n## Available Image Resources\n");
                enhancedPromptBuilder.append("Please use the following images in the generated website, ");
                enhancedPromptBuilder.append("place them in appropriate positions:\n");
                enhancedPromptBuilder.append(imageListStr);
            }
            String enhancedPrompt = enhancedPromptBuilder.toString();
            context.setCurrentStep("Prompt Enhancement");
            context.setEnhancedPrompt(enhancedPrompt);
            log.info("Prompt enhancement completed, length: {}", enhancedPrompt.length());
            return WorkflowContext.saveContext(context);
        });
    }
}