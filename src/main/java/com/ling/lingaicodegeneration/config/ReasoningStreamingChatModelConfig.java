package com.ling.lingaicodegeneration.config;

import com.ling.lingaicodegeneration.monitor.AiModelMonitorListener;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import jakarta.annotation.Resource;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.util.List;

/**
 * Reasoning streaming model config for Vue project generation with tool calling.
 * Uses @Scope("prototype") — each request gets a fresh StreamingChatModel instance,
 * avoiding the serialization bottleneck in the underlying SpringRestClient.execute().
 *
 * Also defines the routingChatModel (plain ChatModel, no JSON format restriction).
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "langchain4j.open-ai.reasoning-streaming-chat-model")
public class ReasoningStreamingChatModelConfig {

    private String baseUrl;
    private String apiKey;
    private String modelName;
    private Integer maxTokens;
    private Double temperature;
    private Boolean logRequests = false;
    private Boolean logResponses = false;

    /**
     * Reasoning streaming model for Vue project generation.
     * Dev: deepseek-chat (fast), Prod: deepseek-reasoner
     * @Scope("prototype") ensures a new instance per injection/getBean() call.
     */

    @Resource
    private AiModelMonitorListener aiModelMonitorListener;

    @Bean
    @Scope("prototype")
    public OpenAiStreamingChatModel reasoningStreamingChatModel() {
        return OpenAiStreamingChatModel.builder()
                .apiKey(apiKey)
                .baseUrl(baseUrl)
                .modelName(modelName)
                .maxTokens(maxTokens)
                .temperature(temperature)
                .logRequests(logRequests)
                .logResponses(logResponses)
                .listeners(List.of(aiModelMonitorListener))
                .build();
    }
}