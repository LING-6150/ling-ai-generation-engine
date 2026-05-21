package com.ling.lingaicodegeneration.config;

import com.ling.lingaicodegeneration.monitor.AiModelMonitorListener;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Review model config — deliberately uses a different provider from CodeGenAgent
 * to get an adversarial, heterogeneous perspective during code review.
 *
 * Switch provider via: review.model.provider=openai (default) | qwen
 *
 * OpenAI:  set OPENAI_API_KEY env var
 * Qwen:    set QWEN_API_KEY env var, change review.model.provider=qwen
 */
@Configuration
public class ReviewModelConfig {

    @Resource
    private AiModelMonitorListener aiModelMonitorListener;

    // ── OpenAI GPT-4o-mini (default) ────────────────────────────────────────

    @Value("${langchain4j.review-model.openai.base-url:https://api.openai.com/v1}")
    private String openAiBaseUrl;

    @Value("${langchain4j.review-model.openai.api-key:${OPENAI_API_KEY:placeholder}}")
    private String openAiApiKey;

    @Value("${langchain4j.review-model.openai.model-name:gpt-4o-mini}")
    private String openAiModelName;

    @Value("${langchain4j.review-model.openai.max-tokens:4096}")
    private Integer openAiMaxTokens;

    @Bean("reviewChatModel")
    @ConditionalOnProperty(name = "review.model.provider", havingValue = "openai", matchIfMissing = true)
    public ChatModel openAiReviewChatModel() {
        return OpenAiChatModel.builder()
                .baseUrl(openAiBaseUrl)
                .apiKey(openAiApiKey)
                .modelName(openAiModelName)
                .maxTokens(openAiMaxTokens)
                .logRequests(false)
                .logResponses(false)
                .listeners(List.of(aiModelMonitorListener))
                .build();
    }

    // ── Qwen (通义千问) fallback ─────────────────────────────────────────────

    @Value("${langchain4j.review-model.qwen.base-url:https://dashscope.aliyuncs.com/compatible-mode/v1}")
    private String qwenBaseUrl;

    @Value("${langchain4j.review-model.qwen.api-key:${QWEN_API_KEY:placeholder}}")
    private String qwenApiKey;

    @Value("${langchain4j.review-model.qwen.model-name:qwen-turbo}")
    private String qwenModelName;

    @Value("${langchain4j.review-model.qwen.max-tokens:4096}")
    private Integer qwenMaxTokens;

    @Bean("reviewChatModel")
    @ConditionalOnProperty(name = "review.model.provider", havingValue = "qwen")
    public ChatModel qwenReviewChatModel() {
        return OpenAiChatModel.builder()
                .baseUrl(qwenBaseUrl)
                .apiKey(qwenApiKey)
                .modelName(qwenModelName)
                .maxTokens(qwenMaxTokens)
                .logRequests(false)
                .logResponses(false)
                .listeners(List.of(aiModelMonitorListener))
                .build();
    }
}
