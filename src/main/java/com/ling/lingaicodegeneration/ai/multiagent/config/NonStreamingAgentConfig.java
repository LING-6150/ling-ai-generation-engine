package com.ling.lingaicodegeneration.ai.multiagent.config;

import com.ling.lingaicodegeneration.ai.multiagent.llm.PlannerLlmService;
import com.ling.lingaicodegeneration.ai.multiagent.llm.RequirementLlmService;
import com.ling.lingaicodegeneration.ai.multiagent.llm.ReviewLlmService;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Creates LangChain4j AiService beans for non-streaming agents.
 *
 * RequirementLlmService / PlannerLlmService → structuredRoutingChatModel
 *   (DeepSeek, json_object mode, max_tokens=512)
 *
 * ReviewLlmService → reviewChatModel
 *   (OpenAI GPT-4o-mini, json_schema mode — activates RESPONSE_FORMAT_JSON_SCHEMA
 *   capability for reliable nested POJO deserialization of ReviewReport)
 *
 * All ChatModel.chat() calls are thread-safe; singleton beans are appropriate.
 */
@Configuration
public class NonStreamingAgentConfig {

    @Resource(name = "structuredRoutingChatModel")
    private ChatModel structuredRoutingChatModel;

    @Resource(name = "reviewChatModel")
    private ChatModel reviewChatModel;

    @Bean
    public RequirementLlmService requirementLlmService() {
        return AiServices.builder(RequirementLlmService.class)
                .chatModel(structuredRoutingChatModel)
                .build();
    }

    @Bean
    public PlannerLlmService plannerLlmService() {
        return AiServices.builder(PlannerLlmService.class)
                .chatModel(structuredRoutingChatModel)
                .build();
    }

    @Bean
    public ReviewLlmService reviewLlmService() {
        return AiServices.builder(ReviewLlmService.class)
                .chatModel(reviewChatModel)
                .build();
    }
}
