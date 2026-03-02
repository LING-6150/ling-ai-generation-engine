package com.ling.lingaicodegeneration.monitor;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Collects AI model metrics and registers them with Micrometer.
 * Micrometer then exposes them via /actuator/prometheus for Prometheus to scrape.
 *
 * Why cache the metric objects?
 * Creating a new Counter/Timer object for every request is expensive.
 * Micrometer also throws an exception if you try to register the same metric
 * with the same tags twice. We cache by a composite key (userId_appId_model_...)
 * so each unique combination gets exactly one metric object, reused across requests.
 *
 * Metrics exposed:
 * - ai_model_requests_total{user_id, app_id, model_name, status} — request count
 * - ai_model_errors_total{user_id, app_id, model_name, error_message} — error count
 * - ai_model_tokens_total{user_id, app_id, model_name, token_type} — token usage
 * - ai_model_response_duration_seconds{user_id, app_id, model_name} — response time
 */
@Component
@Slf4j
public class AiModelMetricsCollector {

    @Resource
    private MeterRegistry meterRegistry;

    // Cache metric objects to avoid duplicate registration and reduce overhead
    private final ConcurrentMap<String, Counter> requestCountersCache = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, Counter> errorCountersCache = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, Counter> tokenCountersCache = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, Timer> responseTimersCache = new ConcurrentHashMap<>();

    /**
     * Record a request with its status (started / success / error).
     * Calling this twice with status="started" and status="success" gives
     * visibility into in-flight requests (started - success = currently processing).
     */
    public void recordRequest(String userId, String appId, String modelName, String status) {
        String key = String.format("%s_%s_%s_%s", userId, appId, modelName, status);
        Counter counter = requestCountersCache.computeIfAbsent(key, k ->
                Counter.builder("ai_model_requests_total")
                        .description("Total number of AI model requests")
                        .tag("user_id", userId)
                        .tag("app_id", appId)
                        .tag("model_name", modelName)
                        .tag("status", status)
                        .register(meterRegistry)
        );
        counter.increment();
    }

    /**
     * Record an error. Separate from request status so we can tag
     * the specific error message for easier debugging in Grafana.
     */
    public void recordError(String userId, String appId, String modelName, String errorMessage) {
        // Truncate error message to avoid cardinality explosion in Prometheus
        String truncatedError = errorMessage != null && errorMessage.length() > 100
                ? errorMessage.substring(0, 100)
                : errorMessage;
        String key = String.format("%s_%s_%s_%s", userId, appId, modelName, truncatedError);
        Counter counter = errorCountersCache.computeIfAbsent(key, k ->
                Counter.builder("ai_model_errors_total")
                        .description("Total number of AI model errors")
                        .tag("user_id", userId)
                        .tag("app_id", appId)
                        .tag("model_name", modelName)
                        .tag("error_message", truncatedError != null ? truncatedError : "unknown")
                        .register(meterRegistry)
        );
        counter.increment();
    }

    /**
     * Record token usage. token_type can be "input", "output", or "total".
     * Tracking input/output separately helps identify whether cost growth
     * is driven by longer prompts (input) or longer responses (output).
     */
    public void recordTokenUsage(String userId, String appId, String modelName,
                                 String tokenType, long tokenCount) {
        String key = String.format("%s_%s_%s_%s", userId, appId, modelName, tokenType);
        Counter counter = tokenCountersCache.computeIfAbsent(key, k ->
                Counter.builder("ai_model_tokens_total")
                        .description("Total number of tokens consumed by AI model")
                        .tag("user_id", userId)
                        .tag("app_id", appId)
                        .tag("model_name", modelName)
                        .tag("token_type", tokenType)
                        .register(meterRegistry)
        );
        counter.increment(tokenCount);
    }

    /**
     * Record response time as a Timer (histogram + summary).
     * Timer automatically tracks count, sum, max, and percentiles,
     * allowing Grafana to show p50/p95/p99 latency distributions.
     */
    public void recordResponseTime(String userId, String appId, String modelName, Duration duration) {
        String key = String.format("%s_%s_%s", userId, appId, modelName);
        Timer timer = responseTimersCache.computeIfAbsent(key, k ->
                Timer.builder("ai_model_response_duration_seconds")
                        .description("AI model response time in seconds")
                        .tag("user_id", userId)
                        .tag("app_id", appId)
                        .tag("model_name", modelName)
                        .register(meterRegistry)
        );
        timer.record(duration);
    }
}