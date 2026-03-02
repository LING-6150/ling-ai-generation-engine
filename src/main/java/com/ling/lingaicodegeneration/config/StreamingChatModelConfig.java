package com.ling.lingaicodegeneration.config;

import com.ling.lingaicodegeneration.monitor.AiModelMonitorListener;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import jakarta.annotation.Resource;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.util.List;

/**
 * Streaming chat model config for normal code generation (HTML / MULTI_FILE).
 * Uses @Scope("prototype") so each call to getBean() returns a fresh instance,
 * solving the concurrency bottleneck caused by StreamingChatModel's internal
 * synchronous SSE parsing (SpringRestClient.execute()).
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "langchain4j.open-ai.streaming-chat-model")


public class StreamingChatModelConfig {

    private String baseUrl;
    private String apiKey;
    private String modelName;
    private Integer maxTokens;
    private Boolean logRequests;
    private Boolean logResponses;

    @Resource
    private AiModelMonitorListener aiModelMonitorListener;

    @Bean
    @Scope("prototype")
    public OpenAiStreamingChatModel openAiStreamingChatModel() {
        return OpenAiStreamingChatModel.builder()
                .apiKey(apiKey)
                .baseUrl(baseUrl)
                .modelName(modelName)
                .maxTokens(maxTokens)
                .logRequests(logRequests)
                .logResponses(logResponses)
                .listeners(List.of(aiModelMonitorListener))
                .build();
    }
}