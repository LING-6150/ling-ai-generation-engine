package com.ling.lingaicodegeneration.config;

import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "langchain4j.open-ai.chat-model")
public class ReasoningStreamingChatModelConfig {

    private String baseUrl;
    private String apiKey;

    /**
     * Routing chat model - plain text output, no JSON format restriction.
     * Used by AiCodeGenTypeRoutingService to return enum values directly.
     */
    @Bean
    public OpenAiChatModel routingChatModel() {
        return OpenAiChatModel.builder()
                .apiKey(apiKey)
                .baseUrl(baseUrl)
                .modelName("deepseek-chat")
                .maxTokens(256)
                .logRequests(true)
                .logResponses(true)
                .build();
    }

    /**
     * Reasoning streaming model for Vue project generation with tool calling.
     * Dev: deepseek-chat (fast), Prod: deepseek-reasoner
     */
    @Bean
    public OpenAiStreamingChatModel reasoningStreamingChatModel() {
        return OpenAiStreamingChatModel.builder()
                .apiKey(apiKey)
                .baseUrl(baseUrl)
                .modelName("deepseek-chat")
                .maxTokens(8192)
                .logRequests(true)
                .logResponses(true)
                .build();
    }
}