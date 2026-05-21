package com.ling.lingaicodegeneration.ai.multiagent.agent;

import com.ling.lingaicodegeneration.ai.ImagePlanningService;
import com.ling.lingaicodegeneration.ai.langgraph4j.model.ImageCollectionPlan;
import com.ling.lingaicodegeneration.ai.tools.ImageSearchTool;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

@Slf4j
@Component
public class AssetAgent {

    @Resource
    private ImagePlanningService imagePlanningService;

    @Resource
    private ImageSearchTool imageSearchTool;

    @Resource(name = "imageSearchExecutor")
    private ExecutorService imageSearchExecutor;

    public String execute(String prompt, AgentContext ctx) {
        log.info("AssetAgent executing, appId: {}", ctx.appId());
        try {
            ImageCollectionPlan plan = imagePlanningService.planImageCollection(prompt);
            List<String> keywords = plan.getContentImageKeywords();
            if (keywords == null || keywords.isEmpty()) {
                log.warn("AssetAgent: no keywords generated, returning empty");
                return "";
            }
            log.info("AssetAgent: {} keywords: {}", keywords.size(), keywords);

            List<CompletableFuture<String>> futures = keywords.stream()
                    .map(keyword -> CompletableFuture.supplyAsync(() -> {
                        try {
                            var results = imageSearchTool.searchContentImages(keyword);
                            if (results == null || results.isEmpty()) return "";
                            return results.stream()
                                    .limit(3)
                                    .map(img -> img.getUrl())
                                    .collect(Collectors.joining("\n"));
                        } catch (Exception e) {
                            log.warn("AssetAgent: image search failed for '{}': {}",
                                    keyword, e.getMessage());
                            return "";
                        }
                    }, imageSearchExecutor))
                    .collect(Collectors.toList());

            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

            List<String> allResults = new ArrayList<>();
            for (CompletableFuture<String> future : futures) {
                String result = future.get();
                if (result != null && !result.isBlank()) {
                    allResults.add(result);
                }
            }
            String imageListStr = String.join("\n", allResults);
            log.info("AssetAgent completed: {} keywords, imageList length: {}",
                    keywords.size(), imageListStr.length());
            return imageListStr;

        } catch (Exception e) {
            log.error("AssetAgent failed: {}", e.getMessage(), e);
            return "";
        }
    }
}
