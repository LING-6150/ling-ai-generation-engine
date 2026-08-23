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
 * Metric labels are deliberately restricted to bounded dimensions. Request-scoped
 * identifiers and raw error messages belong in structured logs, not Prometheus:
 * every unique label combination creates a new time series and a new cached Meter.
 *
 * Metrics exposed:
 * - ai_model_requests_total{model_name, status, agent_name} — request count
 * - ai_model_errors_total{model_name, error_type, agent_name} — error count
 * - ai_model_tokens_total{model_name, token_type, agent_name} — token usage
 * - ai_agent_prompt_chars_total{model_name, agent_name} — prompt characters
 * - ai_agent_prompt_*{model_name, agent_name} — prompt composition
 * - ai_model_response_duration_seconds{model_name, agent_name} — response time
 * - ai_workflow_errors_total{agent_name, error_type, context_pruning} — workflow errors
 *
 * agent_name cardinality: 6 fixed values (RequirementAgent / AssetAgent / PlannerAgent /
 * CodeGenAgent / ReviewAgent / RefineAgent) + "unknown" for non-orchestrated calls.
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
    private final ConcurrentMap<String, Counter> promptCharCountersCache = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, Counter> promptCompositionCountersCache = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, Counter> workflowErrorCountersCache = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, Timer> responseTimersCache = new ConcurrentHashMap<>();

    /**
     * Record a request with its status (started / success / error).
     * Calling this twice with status="started" and status="success" gives
     * visibility into in-flight requests (started - success = currently processing).
     */
    public void recordRequest(String modelName, String status, String agentName) {
        String key = String.format("%s_%s_%s", modelName, status, agentName);
        Counter counter = requestCountersCache.computeIfAbsent(key, k ->
                Counter.builder("ai_model_requests_total")
                        .description("Total number of AI model requests")
                        .tag("model_name", modelName)
                        .tag("status", status)
                        .tag("agent_name", agentName)
                        .register(meterRegistry)
        );
        counter.increment();
    }

    /**
     * Record an error. errorType is intentionally low-cardinality so provider
     * infrastructure failures can be tracked without exploding Prometheus labels.
     */
    public void recordError(String modelName, String errorType, String agentName) {
        String normalizedErrorType = errorType != null ? errorType : "unknown";
        String key = String.format("%s_%s_%s", modelName, normalizedErrorType, agentName);
        Counter counter = errorCountersCache.computeIfAbsent(key, k ->
                Counter.builder("ai_model_errors_total")
                        .description("Total number of AI model errors")
                        .tag("model_name", modelName)
                        .tag("error_type", normalizedErrorType)
                        .tag("agent_name", agentName)
                        .register(meterRegistry)
        );
        counter.increment();
    }

    /**
     * Record token usage. token_type can be "input", "output", or "total".
     * Tracking input/output separately helps identify whether cost growth
     * is driven by longer prompts (input) or longer responses (output).
     */
    public void recordTokenUsage(String modelName, String tokenType,
                                 long tokenCount, String agentName) {
        String key = String.format("%s_%s_%s", modelName, tokenType, agentName);
        Counter counter = tokenCountersCache.computeIfAbsent(key, k ->
                Counter.builder("ai_model_tokens_total")
                        .description("Total number of tokens consumed by AI model")
                        .tag("model_name", modelName)
                        .tag("token_type", tokenType)
                        .tag("agent_name", agentName)
                        .register(meterRegistry)
        );
        counter.increment(tokenCount);
    }

    /**
     * Record outgoing prompt characters for diagnostic attribution.
     * The value is local character count only; it must not call an external
     * tokenizer/model because that would perturb the metric being studied.
     */
    public void recordPromptChars(String modelName, long promptChars, String agentName) {
        if (promptChars <= 0) {
            return;
        }
        String key = String.format("%s_%s", modelName, agentName);
        Counter counter = promptCharCountersCache.computeIfAbsent(key, k ->
                Counter.builder("ai_agent_prompt_chars_total")
                        .description("Total outgoing prompt characters by AI agent")
                        .tag("model_name", modelName)
                        .tag("agent_name", agentName)
                        .register(meterRegistry)
        );
        counter.increment(promptChars);
    }

    /**
     * Record outgoing prompt composition for diagnostic attribution.
     * The buckets are computed from the already-built chat request, so this is
     * read-only instrumentation and must not modify generation behavior.
     */
    public void recordPromptComposition(String modelName,
                                        long systemChars, long memoryChars, long userChars,
                                        long memoryMessages, String agentName) {
        recordPromptCompositionCounter(
                "ai_agent_prompt_system_chars_total",
                "Total outgoing system prompt characters by AI agent",
                modelName, agentName, systemChars);
        recordPromptCompositionCounter(
                "ai_agent_prompt_memory_chars_total",
                "Total outgoing memory/history prompt characters by AI agent",
                modelName, agentName, memoryChars);
        recordPromptCompositionCounter(
                "ai_agent_prompt_user_chars_total",
                "Total outgoing current user prompt characters by AI agent",
                modelName, agentName, userChars);
        recordPromptCompositionCounter(
                "ai_agent_prompt_memory_messages_total",
                "Total outgoing memory/history message count by AI agent",
                modelName, agentName, memoryMessages);
    }

    private void recordPromptCompositionCounter(String metricName, String description,
                                                String modelName, String agentName, long value) {
        if (value <= 0) {
            return;
        }
        String key = String.format("%s_%s_%s", metricName, modelName, agentName);
        Counter counter = promptCompositionCountersCache.computeIfAbsent(key, k ->
                Counter.builder(metricName)
                        .description(description)
                        .tag("model_name", modelName)
                        .tag("agent_name", agentName)
                        .register(meterRegistry)
        );
        counter.increment(value);
    }

    public void recordWorkflowError(String agentName, String errorType, boolean contextPruning) {
        String normalizedErrorType = errorType != null ? errorType : "workflow_error";
        String key = String.format("%s_%s_%s", agentName, normalizedErrorType, contextPruning);
        Counter counter = workflowErrorCountersCache.computeIfAbsent(key, k ->
                Counter.builder("ai_workflow_errors_total")
                        .description("Total number of AI workflow-layer errors")
                        .tag("agent_name", agentName)
                        .tag("error_type", normalizedErrorType)
                        .tag("context_pruning", String.valueOf(contextPruning))
                        .register(meterRegistry)
        );
        counter.increment();
    }

    /**
     * Record response time as a Timer (histogram + summary).
     * Timer automatically tracks count, sum, max, and percentiles,
     * allowing Grafana to show p50/p95/p99 latency distributions.
     */
    public void recordResponseTime(String modelName, Duration duration, String agentName) {
        String key = String.format("%s_%s", modelName, agentName);
        Timer timer = responseTimersCache.computeIfAbsent(key, k ->
                Timer.builder("ai_model_response_duration_seconds")
                        .description("AI model response time in seconds")
                        .tag("model_name", modelName)
                        .tag("agent_name", agentName)
                        .register(meterRegistry)
        );
        timer.record(duration);
    }
}
