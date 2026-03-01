package com.ling.lingaicodegeneration.ai.tools;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.ling.lingaicodegeneration.ai.langgraph4j.model.ImageResource;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.agent.tool.P;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Tool for searching content images from Pexels
 */
@Slf4j
@Component
public class ImageSearchTool {

    private static final String PEXELS_API_URL = "https://api.pexels.com/v1/search";

    @Value("${pexels.api-key}")
    private String pexelsApiKey;

    @Tool("Search for content-related images, used for website content display")
    public List<ImageResource> searchContentImages(
            @P("Search query keywords") String query) {
        List<ImageResource> imageList = new ArrayList<>();
        int searchCount = 12;
        try {
            HttpResponse response = HttpRequest.get(PEXELS_API_URL)
                    .header("Authorization", pexelsApiKey)
                    .form("query", query)
                    .form("per_page", searchCount)
                    .form("page", 1)
                    .execute();
            if (response.isOk()) {
                JSONObject result = JSONUtil.parseObj(response.body());
                JSONArray photos = result.getJSONArray("photos");
                for (int i = 0; i < photos.size(); i++) {
                    JSONObject photo = photos.getJSONObject(i);
                    JSONObject src = photo.getJSONObject("src");
                    imageList.add(ImageResource.builder()
                            .description(photo.getStr("alt", query))
                            .url(src.getStr("medium"))
                            .build());
                }
            }
        } catch (Exception e) {
            log.error("Pexels API error: {}", e.getMessage(), e);
        }
        return imageList;
    }
}