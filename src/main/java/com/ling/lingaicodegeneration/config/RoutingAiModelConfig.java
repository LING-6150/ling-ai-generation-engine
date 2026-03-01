package com.ling.lingaicodegeneration.config;

import dev.langchain4j.model.openai.OpenAiChatModel;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * Routing AI model config.
 * Uses a lightweight model for simple classification tasks (selecting code gen type).
 * No response_format: json_object restriction — routing returns an enum value directly.
 * @Scope("prototype") for thread safety.
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "langchain4j.open-ai.routing-chat-model")
public class RoutingAiModelConfig {

    private String baseUrl;
    private String apiKey;
    private String modelName;
    private Integer maxTokens;
    private Double temperature;
    private Boolean logRequests = false;
    private Boolean logResponses = false;

    /**
     * Routing ChatModel — plain text output for enum-based routing decisions.
     * @Scope("prototype") — each factory call gets a fresh instance.
     */
    @Bean
    @Scope("prototype")
    public OpenAiChatModel routingChatModelPrototype() {
        return OpenAiChatModel.builder()
                .apiKey(apiKey)
                .baseUrl(baseUrl)
                .modelName(modelName)
                .maxTokens(maxTokens)
                .temperature(temperature)
                .logRequests(logRequests)
                .logResponses(logResponses)
                .build();
    }
}