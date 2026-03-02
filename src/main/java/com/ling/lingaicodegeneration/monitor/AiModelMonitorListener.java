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
 * LangChain4j ChatModelListener that hooks into the AI model request lifecycle.
 * Works for BOTH ChatModel and StreamingChatModel — LangChain4j uses the same
 * ChatModelListener interface for both, so one implementation covers all cases.
 *
 * Lifecycle:
 *   onRequest  → called before sending request to LLM (same thread as caller)
 *   onResponse → called after successful response (may be different thread for streaming)
 *   onError    → called if request fails (may be different thread for streaming)
 *
 * Cross-thread context passing strategy:
 * - ThreadLocal (MonitorContextHolder) works for onRequest since it's on the caller thread
 * - For onResponse/onError, we copy the context into requestContext.attributes() in onRequest
 *   so it's available regardless of which thread handles the response
 * - This is safer than relying on ThreadLocal for streaming responses
 */
@Component
@Slf4j
public class AiModelMonitorListener implements ChatModelListener {

    private static final String REQUEST_START_TIME_KEY = "monitor_request_start_time";
    private static final String MONITOR_CONTEXT_KEY = "monitor_context";

    @Resource
    private AiModelMetricsCollector aiModelMetricsCollector;

    @Override
    public void onRequest(ChatModelRequestContext requestContext) {
        // Record request start time for latency calculation
        requestContext.attributes().put(REQUEST_START_TIME_KEY, Instant.now());

        // Get context from ThreadLocal (safe here — onRequest runs on caller thread)
        MonitorContext context = MonitorContextHolder.getContext();
        if (context == null) {
            // Context may be null for non-user-triggered calls (e.g. routing)
            context = MonitorContext.builder()
                    .userId("system")
                    .appId("system")
                    .build();
        }

        // Store context in attributes so onResponse/onError can access it
        // even if they run on a different thread (streaming case)
        requestContext.attributes().put(MONITOR_CONTEXT_KEY, context);

        String modelName = requestContext.chatRequest().modelName() != null
                ? requestContext.chatRequest().modelName()
                : "unknown";

        aiModelMetricsCollector.recordRequest(
                context.getUserId(), context.getAppId(), modelName, "started");

        log.debug("AI request started - userId: {}, appId: {}, model: {}",
                context.getUserId(), context.getAppId(), modelName);
    }

    @Override
    public void onResponse(ChatModelResponseContext responseContext) {
        Map<Object, Object> attributes = responseContext.attributes();

        // Get context from attributes (NOT ThreadLocal — may be different thread)
        MonitorContext context = (MonitorContext) attributes.get(MONITOR_CONTEXT_KEY);
        if (context == null) {
            log.warn("MonitorContext not found in response attributes, skipping metrics");
            return;
        }

        String modelName = responseContext.chatResponse().modelName() != null
                ? responseContext.chatResponse().modelName()
                : "unknown";

        // Record successful request
        aiModelMetricsCollector.recordRequest(
                context.getUserId(), context.getAppId(), modelName, "success");

        // Record response time
        recordResponseTime(attributes, context.getUserId(), context.getAppId(), modelName);

        // Record token usage
        recordTokenUsage(responseContext, context.getUserId(), context.getAppId(), modelName);

        log.debug("AI request completed - userId: {}, appId: {}, model: {}",
                context.getUserId(), context.getAppId(), modelName);
    }

    @Override
    public void onError(ChatModelErrorContext errorContext) {
        Map<Object, Object> attributes = errorContext.attributes();

        // Get context from attributes (safer than ThreadLocal for streaming)
        MonitorContext context = (MonitorContext) attributes.get(MONITOR_CONTEXT_KEY);
        if (context == null) {
            // Fallback to ThreadLocal as last resort
            context = MonitorContextHolder.getContext();
        }
        if (context == null) {
            log.warn("MonitorContext not found in error context, skipping metrics");
            return;
        }

        String modelName = errorContext.chatRequest().modelName() != null
                ? errorContext.chatRequest().modelName()
                : "unknown";
        String errorMessage = errorContext.error() != null
                ? errorContext.error().getMessage()
                : "unknown error";

        // Record failed request and error details
        aiModelMetricsCollector.recordRequest(
                context.getUserId(), context.getAppId(), modelName, "error");
        aiModelMetricsCollector.recordError(
                context.getUserId(), context.getAppId(), modelName, errorMessage);

        // Record response time even for errors (helps identify timeout patterns)
        recordResponseTime(attributes, context.getUserId(), context.getAppId(), modelName);

        log.warn("AI request failed - userId: {}, appId: {}, model: {}, error: {}",
                context.getUserId(), context.getAppId(), modelName, errorMessage);
    }

    private void recordResponseTime(Map<Object, Object> attributes,
                                    String userId, String appId, String modelName) {
        Instant startTime = (Instant) attributes.get(REQUEST_START_TIME_KEY);
        if (startTime == null) {
            log.warn("Request start time not found, cannot record response time");
            return;
        }
        Duration responseTime = Duration.between(startTime, Instant.now());
        aiModelMetricsCollector.recordResponseTime(userId, appId, modelName, responseTime);
    }

    private void recordTokenUsage(ChatModelResponseContext responseContext,
                                  String userId, String appId, String modelName) {
        try {
            TokenUsage tokenUsage = responseContext.chatResponse().metadata().tokenUsage();
            if (tokenUsage == null) {
                return;
            }
            if (tokenUsage.inputTokenCount() != null) {
                aiModelMetricsCollector.recordTokenUsage(
                        userId, appId, modelName, "input", tokenUsage.inputTokenCount());
            }
            if (tokenUsage.outputTokenCount() != null) {
                aiModelMetricsCollector.recordTokenUsage(
                        userId, appId, modelName, "output", tokenUsage.outputTokenCount());
            }
            if (tokenUsage.totalTokenCount() != null) {
                aiModelMetricsCollector.recordTokenUsage(
                        userId, appId, modelName, "total", tokenUsage.totalTokenCount());
            }
        } catch (Exception e) {
            log.warn("Failed to record token usage: {}", e.getMessage());
        }
    }
}