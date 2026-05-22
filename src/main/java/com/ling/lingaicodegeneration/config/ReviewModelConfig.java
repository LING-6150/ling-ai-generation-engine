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
        // 注意：Dashscope 兼容层只支持 json_object，不支持 json_schema（OpenAI Structured Outputs）。
        // 文档来源：https://www.alibabacloud.com/help/en/model-studio/qwen-structured-output
        // 因此此分支不设置 responseFormat，DefaultAiServices 将使用 prompt 注入模式。
        // 可靠性低于 OpenAI 分支，若 ReviewReport 解析频繁失败，建议切换回 openai provider。
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
