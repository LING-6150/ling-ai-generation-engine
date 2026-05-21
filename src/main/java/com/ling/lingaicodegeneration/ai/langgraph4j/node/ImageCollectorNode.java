package com.ling.lingaicodegeneration.ai.langgraph4j.node;

import com.ling.lingaicodegeneration.ai.ImagePlanningService;
import com.ling.lingaicodegeneration.ai.langgraph4j.model.ImageCollectionPlan;
import com.ling.lingaicodegeneration.ai.langgraph4j.state.WorkflowContext;
import com.ling.lingaicodegeneration.ai.tools.ImageSearchTool;
import com.ling.lingaicodegeneration.utils.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.bsc.langgraph4j.action.AsyncNodeAction;
import org.bsc.langgraph4j.state.AgentState;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

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
                // Step 1: AI 生成搜索计划，获取关键词列表
                ImagePlanningService planningService =
                        SpringContextUtil.getBean(ImagePlanningService.class);
                ImageCollectionPlan plan =
                        planningService.planImageCollection(originalPrompt);

                List<String> keywords = plan.getContentImageKeywords();
                if (keywords == null || keywords.isEmpty()) {
                    log.warn("No keywords generated, skipping image collection");
                    context.setCurrentStep("Image Collection");
                    context.setImageListStr("");
                    return WorkflowContext.saveContext(context);
                }

                log.info("Image collection plan: {} keywords: {}",
                        keywords.size(), keywords);

                // Step 2: 为每个关键词创建 CompletableFuture 并发任务
                ImageSearchTool imageSearchTool =
                        SpringContextUtil.getBean(ImageSearchTool.class);
                ExecutorService imageExecutor =
                        SpringContextUtil.getBean("imageSearchExecutor", ExecutorService.class);

                List<CompletableFuture<String>> futures = keywords.stream()
                        .map(keyword -> CompletableFuture.supplyAsync(() -> {
                            try {
                                log.info("Searching images for keyword: {}", keyword);
                                var results = imageSearchTool.searchContentImages(keyword);
                                if (results == null || results.isEmpty()) return "";
                                // 每个关键词最多取3张图片
                                return results.stream()
                                        .limit(3)
                                        .map(img -> img.getUrl())
                                        .collect(Collectors.joining("\n"));
                            } catch (Exception e) {
                                log.warn("Image search failed for keyword {}: {}",
                                        keyword, e.getMessage());
                                return ""; // 单个关键词失败不影响整体
                            }
                        }, imageExecutor))
                        .collect(Collectors.toList());

                // Step 3: 等待所有并发任务完成
                CompletableFuture.allOf(
                        futures.toArray(new CompletableFuture[0])
                ).join();

                // Step 4: 汇总所有结果
                List<String> allResults = new ArrayList<>();
                for (CompletableFuture<String> future : futures) {
                    String result = future.get();
                    if (result != null && !result.isBlank()) {
                        allResults.add(result);
                    }
                }
                imageListStr = String.join("\n", allResults);
                log.info("Image collection completed, {} keywords, result length: {}",
                        keywords.size(), imageListStr.length());

            } catch (Exception e) {
                log.error("Image collection failed: {}", e.getMessage(), e);
                // 失败不阻断流程，imageListStr 保持空字符串
            }

            context.setCurrentStep("Image Collection");
            context.setImageListStr(imageListStr);
            return WorkflowContext.saveContext(context);
        });
    }
}