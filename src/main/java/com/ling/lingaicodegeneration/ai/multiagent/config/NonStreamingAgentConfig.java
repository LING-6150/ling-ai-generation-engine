package com.ling.lingaicodegeneration.ai.multiagent.config;

import com.ling.lingaicodegeneration.ai.multiagent.llm.PlannerLlmService;
import com.ling.lingaicodegeneration.ai.multiagent.llm.RequirementLlmService;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Creates LangChain4j AiService beans for the two lightweight non-streaming agents.
 * Both use structuredRoutingChatModel (json_object mode, max_tokens=512) for reliable
 * structured output parsing. Singleton beans — ChatModel.chat() is thread-safe.
 */
@Configuration
public class NonStreamingAgentConfig {

    @Resource(name = "structuredRoutingChatModel")
    private ChatModel structuredRoutingChatModel;

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
}
