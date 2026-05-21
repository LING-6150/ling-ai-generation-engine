package com.ling.lingaicodegeneration.multiagent.agent;

import com.ling.lingaicodegeneration.ai.ImagePlanningService;
import com.ling.lingaicodegeneration.ai.langgraph4j.model.ImageCollectionPlan;
import com.ling.lingaicodegeneration.ai.langgraph4j.model.ImageResource;
import com.ling.lingaicodegeneration.ai.multiagent.agent.AgentContext;
import com.ling.lingaicodegeneration.ai.multiagent.agent.AssetAgent;
import com.ling.lingaicodegeneration.ai.tools.ImageSearchTool;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AssetAgentTest {

    @Mock
    private ImagePlanningService imagePlanningService;

    @Mock
    private ImageSearchTool imageSearchTool;

    @InjectMocks
    private AssetAgent assetAgent;

    private ExecutorService executor;

    private final AgentContext ctx = new AgentContext(1L, 1L, 3, "AssetAgent");

    @BeforeEach
    void setUp() {
        executor = Executors.newFixedThreadPool(3);
        ReflectionTestUtils.setField(assetAgent, "imageSearchExecutor", executor);
    }

    @AfterEach
    void tearDown() {
        executor.shutdown();
    }

    @Test
    void execute_normalCase_returnsImageUrls() {
        ImageCollectionPlan plan = new ImageCollectionPlan();
        plan.setContentImageKeywords(List.of("modern office", "team collaboration"));
        when(imagePlanningService.planImageCollection(anyString())).thenReturn(plan);

        ImageResource img1 = ImageResource.builder().url("https://pexels.com/img1.jpg").build();
        ImageResource img2 = ImageResource.builder().url("https://pexels.com/img2.jpg").build();
        when(imageSearchTool.searchContentImages(anyString())).thenReturn(List.of(img1, img2));

        String result = assetAgent.execute("build an office landing page", ctx);

        assertFalse(result.isBlank());
        assertTrue(result.contains("pexels.com"));
    }

    @Test
    void execute_noKeywords_returnsEmpty() {
        ImageCollectionPlan plan = new ImageCollectionPlan();
        plan.setContentImageKeywords(List.of());
        when(imagePlanningService.planImageCollection(anyString())).thenReturn(plan);

        String result = assetAgent.execute("some prompt", ctx);

        assertEquals("", result);
    }

    @Test
    void execute_oneKeywordFails_otherSucceeds() {
        ImageCollectionPlan plan = new ImageCollectionPlan();
        plan.setContentImageKeywords(List.of("good keyword", "bad keyword"));
        when(imagePlanningService.planImageCollection(anyString())).thenReturn(plan);

        ImageResource img = ImageResource.builder().url("https://pexels.com/good.jpg").build();
        when(imageSearchTool.searchContentImages("good keyword")).thenReturn(List.of(img));
        when(imageSearchTool.searchContentImages("bad keyword"))
                .thenThrow(new RuntimeException("API error"));

        String result = assetAgent.execute("some prompt", ctx);

        assertTrue(result.contains("good.jpg"));
    }

    @Test
    void execute_planningServiceThrows_returnsEmpty() {
        when(imagePlanningService.planImageCollection(anyString()))
                .thenThrow(new RuntimeException("LLM timeout"));

        String result = assetAgent.execute("some prompt", ctx);

        assertEquals("", result);
    }
}
