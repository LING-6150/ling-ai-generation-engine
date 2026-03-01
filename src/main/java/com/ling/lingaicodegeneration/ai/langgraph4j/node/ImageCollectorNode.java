package com.ling.lingaicodegeneration.ai.langgraph4j.node;

import com.ling.lingaicodegeneration.ai.ImageCollectionService;
import com.ling.lingaicodegeneration.ai.langgraph4j.state.WorkflowContext;
import com.ling.lingaicodegeneration.utils.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.bsc.langgraph4j.action.AsyncNodeAction;
import org.bsc.langgraph4j.state.AgentState;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.bsc.langgraph4j.action.AsyncNodeAction.node_async;

@Slf4j
public class ImageCollectorNode {

    public static AsyncNodeAction<AgentState> create() {
        return node_async(state -> {
            WorkflowContext context = WorkflowContext.getContext(state);
            log.info("Executing node: Image Collection");
            String originalPrompt = context.getOriginalPrompt();
            String imageListStr = "";
            try {
                ImageCollectionService imageCollectionService =
                        SpringContextUtil.getBean(ImageCollectionService.class);
                imageListStr = imageCollectionService.collectImages(originalPrompt);
                log.info("Image collection completed, result length: {}", imageListStr.length());
            } catch (Exception e) {
                log.error("Image collection failed: {}", e.getMessage(), e);
            }
            context.setCurrentStep("Image Collection");
            context.setImageListStr(imageListStr);
            return WorkflowContext.saveContext(context);
        });
    }
}