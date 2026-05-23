package com.ling.lingaicodegeneration.monitor;

import dev.langchain4j.model.chat.listener.ChatModelErrorContext;
import dev.langchain4j.model.chat.listener.ChatModelListener;
import dev.langchain4j.model.chat.listener.ChatModelRequestContext;
import dev.langchain4j.model.chat.listener.ChatModelResponseContext;
import dev.langchain4j.model.output.TokenUsage;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;

/**
 * LangChain4j ChatModelListener，hook 进 AI 模型请求的生命周期。
 * 同时支持 ChatModel 和 StreamingChatModel —— LangChain4j 对两者使用同一个
 * ChatModelListener 接口，因此一个实现类覆盖所有模型。
 *
 * 生命周期：
 *   onRequest  → 发送请求给 LLM 之前调用（和调用者在同一个线程）
 *   onResponse → 成功响应后调用（流式模式下可能在不同线程）
 *   onError    → 请求失败时调用（流式模式下可能在不同线程）
 *
 * 跨线程上下文传递策略：
 * - ThreadLocal（MonitorContextHolder）在 onRequest 里可以用，因为和调用者同线程
 *   OrchestratorAgent 在每次 agent 调用前 setContext(agentName)，调用后 clearContext()。
 * - 对于 onResponse/onError，我们在 onRequest 里把 context（含 agentName）存入 attributes()
 *   这样无论哪个线程处理响应，都能取到完整的 context。
 */
@Component
@Slf4j
public class AiModelMonitorListener implements ChatModelListener {

    private static final String REQUEST_START_TIME_KEY = "monitor_request_start_time";
    private static final String MONITOR_CONTEXT_KEY    = "monitor_context";

    @Resource
    private AiModelMetricsCollector aiModelMetricsCollector;

    @Resource
    private org.redisson.api.RedissonClient redissonClient;

    @Override
    public void onRequest(ChatModelRequestContext requestContext) {
        requestContext.attributes().put(REQUEST_START_TIME_KEY, Instant.now());

        // onRequest 和调用者同线程，ThreadLocal 可安全读取
        MonitorContext context = MonitorContextHolder.getContext();
        if (context == null) {
            context = MonitorContext.builder().userId("system").appId("system").build();
        }

        // 存入 attributes，onResponse/onError（可能跨线程）从这里取
        requestContext.attributes().put(MONITOR_CONTEXT_KEY, context);

        String modelName = requestContext.chatRequest().modelName() != null
                ? requestContext.chatRequest().modelName() : "unknown";

        aiModelMetricsCollector.recordRequest(
                context.getUserId(), context.getAppId(), modelName, "started",
                context.getAgentName());

        log.debug("AI 请求开始 - userId: {}, appId: {}, model: {}, agent: {}",
                context.getUserId(), context.getAppId(), modelName, context.getAgentName());
    }

    @Override
    public void onResponse(ChatModelResponseContext responseContext) {
        Map<Object, Object> attributes = responseContext.attributes();

        MonitorContext context = (MonitorContext) attributes.get(MONITOR_CONTEXT_KEY);
        if (context == null) {
            log.warn("响应 attributes 中未找到 MonitorContext，跳过指标记录");
            return;
        }

        String modelName = responseContext.chatResponse().modelName() != null
                ? responseContext.chatResponse().modelName() : "unknown";

        aiModelMetricsCollector.recordRequest(
                context.getUserId(), context.getAppId(), modelName, "success",
                context.getAgentName());

        recordResponseTime(attributes, context.getUserId(), context.getAppId(), modelName,
                context.getAgentName());
        recordTokenUsage(responseContext, context.getUserId(), context.getAppId(), modelName,
                context.getAgentName());

        log.debug("AI 请求完成 - userId: {}, appId: {}, model: {}, agent: {}",
                context.getUserId(), context.getAppId(), modelName, context.getAgentName());

        accumulateDailyTokens(context.getUserId(),
                responseContext.chatResponse().metadata().tokenUsage());
    }

    @Override
    public void onError(ChatModelErrorContext errorContext) {
        Map<Object, Object> attributes = errorContext.attributes();

        MonitorContext context = (MonitorContext) attributes.get(MONITOR_CONTEXT_KEY);
        if (context == null) {
            context = MonitorContextHolder.getContext();
        }
        if (context == null) {
            log.warn("错误 context 中未找到 MonitorContext，跳过指标记录");
            return;
        }

        String modelName = errorContext.chatRequest().modelName() != null
                ? errorContext.chatRequest().modelName() : "unknown";
        String errorMessage = errorContext.error() != null
                ? errorContext.error().getMessage() : "unknown error";

        aiModelMetricsCollector.recordRequest(
                context.getUserId(), context.getAppId(), modelName, "error",
                context.getAgentName());
        aiModelMetricsCollector.recordError(
                context.getUserId(), context.getAppId(), modelName, errorMessage,
                context.getAgentName());
        recordResponseTime(attributes, context.getUserId(), context.getAppId(), modelName,
                context.getAgentName());

        log.warn("AI 请求失败 - userId: {}, appId: {}, model: {}, agent: {}, error: {}",
                context.getUserId(), context.getAppId(), modelName,
                context.getAgentName(), errorMessage);
    }

    private void recordResponseTime(Map<Object, Object> attributes,
                                    String userId, String appId, String modelName,
                                    String agentName) {
        Instant startTime = (Instant) attributes.get(REQUEST_START_TIME_KEY);
        if (startTime == null) {
            log.warn("未找到请求开始时间，无法记录响应时间");
            return;
        }
        aiModelMetricsCollector.recordResponseTime(userId, appId, modelName,
                Duration.between(startTime, Instant.now()), agentName);
    }

    private void recordTokenUsage(ChatModelResponseContext responseContext,
                                  String userId, String appId, String modelName,
                                  String agentName) {
        try {
            TokenUsage tokenUsage = responseContext.chatResponse().metadata().tokenUsage();
            if (tokenUsage == null) return;
            if (tokenUsage.inputTokenCount() != null) {
                aiModelMetricsCollector.recordTokenUsage(
                        userId, appId, modelName, "input", tokenUsage.inputTokenCount(), agentName);
            }
            if (tokenUsage.outputTokenCount() != null) {
                aiModelMetricsCollector.recordTokenUsage(
                        userId, appId, modelName, "output", tokenUsage.outputTokenCount(), agentName);
            }
            if (tokenUsage.totalTokenCount() != null) {
                aiModelMetricsCollector.recordTokenUsage(
                        userId, appId, modelName, "total", tokenUsage.totalTokenCount(), agentName);
            }
        } catch (Exception e) {
            log.warn("记录 Token 消耗失败: {}", e.getMessage());
        }
    }

    private void accumulateDailyTokens(String userId, TokenUsage tokenUsage) {
        try {
            if (tokenUsage == null || tokenUsage.totalTokenCount() == null) return;
            if ("system".equals(userId)) return;

            String today = java.time.LocalDate.now()
                    .format(java.time.format.DateTimeFormatter.BASIC_ISO_DATE);
            String key = "daily_token:" + userId + ":" + today;

            org.redisson.api.RAtomicLong counter = redissonClient.getAtomicLong(key);
            counter.addAndGet(tokenUsage.totalTokenCount());
            counter.expire(java.time.Duration.ofHours(25));

            log.debug("今日 Token 累计 - userId: {}, 日期: {}, 本次: {}",
                    userId, today, tokenUsage.totalTokenCount());
        } catch (Exception e) {
            log.warn("累计今日 Token 失败: {}", e.getMessage());
        }
    }
}
