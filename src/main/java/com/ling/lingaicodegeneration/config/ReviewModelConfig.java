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
 * Switch provider via: review.model.provider=zhipu (default) | openai | fallback-deepseek
 *
 * zhipu:            set ZHIPU_API_KEY env var  (GLM-4-Flash, prompt-injection mode)
 * openai:           set OPENAI_API_KEY env var  (GPT-4o-mini, json_schema structured output)
 * fallback-deepseek: set DEEPSEEK_API_KEY env var (deepseek-chat, prompt-injection mode)
 */
@Configuration
public class ReviewModelConfig {

    @Resource
    private AiModelMonitorListener aiModelMonitorListener;

    // ── Zhipu GLM-4-Flash (default) ─────────────────────────────────────────
    // Note: GLM-4-Flash uses OpenAI-compatible API but does NOT support json_schema
    // (OpenAI Structured Outputs). DefaultAiServices falls back to prompt-injection
    // mode for ReviewReport parsing — acceptable reliability for local dev.
    // To get full Structured Outputs, switch to review.model.provider=openai.

    @Value("${langchain4j.review-model.zhipu.base-url:https://open.bigmodel.cn/api/paas/v4}")
    private String zhipuBaseUrl;

    @Value("${langchain4j.review-model.zhipu.api-key:${ZHIPU_API_KEY:placeholder}}")
    private String zhipuApiKey;

    @Value("${langchain4j.review-model.zhipu.model-name:glm-4-flash}")
    private String zhipuModelName;

    @Value("${langchain4j.review-model.zhipu.max-tokens:4096}")
    private Integer zhipuMaxTokens;

    @Bean("reviewChatModel")
    @ConditionalOnProperty(name = "review.model.provider", havingValue = "zhipu", matchIfMissing = true)
    public ChatModel zhipuReviewChatModel() {
        return OpenAiChatModel.builder()
                .baseUrl(zhipuBaseUrl)
                .apiKey(zhipuApiKey)
                .modelName(zhipuModelName)
                .maxTokens(zhipuMaxTokens)
                .logRequests(false)
                .logResponses(false)
                .listeners(List.of(aiModelMonitorListener))
                .build();
    }

    // ── OpenAI GPT-4o-mini ──────────────────────────────────────────────────

    @Value("${langchain4j.review-model.openai.base-url:https://api.openai.com/v1}")
    private String openAiBaseUrl;

    @Value("${langchain4j.review-model.openai.api-key:${OPENAI_API_KEY:placeholder}}")
    private String openAiApiKey;

    @Value("${langchain4j.review-model.openai.model-name:gpt-4o-mini}")
    private String openAiModelName;

    @Value("${langchain4j.review-model.openai.max-tokens:4096}")
    private Integer openAiMaxTokens;

    @Bean("reviewChatModel")
    @ConditionalOnProperty(name = "review.model.provider", havingValue = "openai")
    public ChatModel openAiReviewChatModel() {
        return OpenAiChatModel.builder()
                .baseUrl(openAiBaseUrl)
                .apiKey(openAiApiKey)
                .modelName(openAiModelName)
                .maxTokens(openAiMaxTokens)
                // 必须启用 json_schema 模式以激活 OpenAI Structured Outputs。
                // 验证来源：LangChain4j 1.1.0 OpenAiChatModel.supportedCapabilities() bytecode —
                // 仅当 responseFormat == "json_schema" 时才注册 RESPONSE_FORMAT_JSON_SCHEMA capability。
                // 没有这个配置，DefaultAiServices 回退到 prompt 注入模式，
                // 对 ReviewReport(含 List<Issue> 嵌套结构)的 POJO 解析失败率较高。
                // 勿删。
                .responseFormat("json_schema")
                .logRequests(false)
                .logResponses(false)
                .listeners(List.of(aiModelMonitorListener))
                .build();
    }

    // ── DeepSeek fallback (same provider as CodeGenAgent — emergency use only) ──
    // Use only when zhipu/openai are unavailable. Homogeneous review (DeepSeek
    // reviewing DeepSeek output) reduces adversarial effectiveness.

    @Value("${langchain4j.review-model.fallback-deepseek.base-url:https://api.deepseek.com}")
    private String fallbackDeepSeekBaseUrl;

    @Value("${langchain4j.review-model.fallback-deepseek.api-key:${DEEPSEEK_API_KEY:placeholder}}")
    private String fallbackDeepSeekApiKey;

    @Value("${langchain4j.review-model.fallback-deepseek.model-name:deepseek-chat}")
    private String fallbackDeepSeekModelName;

    @Value("${langchain4j.review-model.fallback-deepseek.max-tokens:4096}")
    private Integer fallbackDeepSeekMaxTokens;

    @Bean("reviewChatModel")
    @ConditionalOnProperty(name = "review.model.provider", havingValue = "fallback-deepseek")
    public ChatModel fallbackDeepSeekReviewChatModel() {
        return OpenAiChatModel.builder()
                .baseUrl(fallbackDeepSeekBaseUrl)
                .apiKey(fallbackDeepSeekApiKey)
                .modelName(fallbackDeepSeekModelName)
                .maxTokens(fallbackDeepSeekMaxTokens)
                .logRequests(false)
                .logResponses(false)
                .listeners(List.of(aiModelMonitorListener))
                .build();
    }
}
