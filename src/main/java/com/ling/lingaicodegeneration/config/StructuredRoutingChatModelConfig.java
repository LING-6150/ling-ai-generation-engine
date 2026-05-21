package com.ling.lingaicodegeneration.config;

import com.ling.lingaicodegeneration.monitor.AiModelMonitorListener;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import jakarta.annotation.Resource;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Structured output chat model for RequirementAgent and PlannerAgent.
 * Uses json_object mode for reliable POJO deserialization, with a smaller
 * max_tokens budget than the main code-generation model.
 * Singleton bean — ChatModel.chat() is thread-safe for concurrent calls.
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "langchain4j.structured-routing-chat-model")
public class StructuredRoutingChatModelConfig {

    private String baseUrl;
    private String apiKey;
    private String modelName;
    private Integer maxTokens;
    private Boolean logRequests = false;
    private Boolean logResponses = false;

    @Resource
    private AiModelMonitorListener aiModelMonitorListener;

    @Bean
    public ChatModel structuredRoutingChatModel() {
        return OpenAiChatModel.builder()
                .baseUrl(baseUrl)
                .apiKey(apiKey)
                .modelName(modelName)
                .maxTokens(maxTokens)
                .responseFormat("json_object")
                .logRequests(logRequests)
                .logResponses(logResponses)
                .listeners(List.of(aiModelMonitorListener))
                .build();
    }
}
