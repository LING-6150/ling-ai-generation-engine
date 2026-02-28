package com.ling.lingaicodegeneration.config;

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
     * 推理流式模型（用于 Vue 项目生成，等工具调用）
     * 开发时用普通对话模型提高速度，生产环境换成 deepseek-reasoner
     */
    @Bean
    public OpenAiStreamingChatModel reasoningStreamingChatModel() {
        // 开发时使用普通模型，速度更快
        final String modelName = "deepseek-chat";
        final int maxTokens = 8192;
        // 生产环境使用：
        // final String modelName = "deepseek-reasoner";
        // final int maxTokens = 32768;
        return OpenAiStreamingChatModel.builder()
                .apiKey(apiKey)
                .baseUrl(baseUrl)
                .modelName(modelName)
                .maxTokens(maxTokens)
                .logRequests(true)
                .logResponses(true)
                .build();
    }
}