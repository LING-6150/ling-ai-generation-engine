package com.ling.lingaicodegeneration.monitor;

import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Duration;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class AiModelMetricsCollectorTest {

    private static final Set<String> FORBIDDEN_HIGH_CARDINALITY_TAGS = Set.of(
            "user_id", "app_id", "error_message"
    );

    private SimpleMeterRegistry registry;
    private AiModelMetricsCollector collector;

    @BeforeEach
    void setUp() {
        registry = new SimpleMeterRegistry();
        collector = new AiModelMetricsCollector();
        ReflectionTestUtils.setField(collector, "meterRegistry", registry);
    }

    @Test
    void recordsOperationalMetricsWithBoundedTagsOnly() {
        collector.recordRequest("deepseek-chat", "started", "CodeGenAgent");
        collector.recordRequest("deepseek-chat", "success", "CodeGenAgent");
        collector.recordError("deepseek-chat", "timeout", "CodeGenAgent");
        collector.recordTokenUsage("deepseek-chat", "input", 120, "CodeGenAgent");
        collector.recordResponseTime(
                "deepseek-chat", Duration.ofMillis(250), "CodeGenAgent");
        collector.recordWorkflowError(
                "OrchestratorAgent", "workflow_empty_stream", true);

        assertEquals(1.0, registry.get("ai_model_requests_total")
                .tag("model_name", "deepseek-chat")
                .tag("status", "success")
                .tag("agent_name", "CodeGenAgent")
                .counter()
                .count());
        assertEquals(1.0, registry.get("ai_model_errors_total")
                .tag("model_name", "deepseek-chat")
                .tag("error_type", "timeout")
                .tag("agent_name", "CodeGenAgent")
                .counter()
                .count());
        assertEquals(120.0, registry.get("ai_model_tokens_total")
                .tag("model_name", "deepseek-chat")
                .tag("token_type", "input")
                .tag("agent_name", "CodeGenAgent")
                .counter()
                .count());
        assertEquals(1.0, registry.get("ai_workflow_errors_total")
                .tag("agent_name", "OrchestratorAgent")
                .tag("error_type", "workflow_empty_stream")
                .tag("context_pruning", "true")
                .counter()
                .count());

        assertNoHighCardinalityTags();
    }

    @Test
    void repeatedCallsAggregateIntoSameLowCardinalitySeries() {
        collector.recordTokenUsage("deepseek-chat", "total", 10, "ReviewAgent");
        collector.recordTokenUsage("deepseek-chat", "total", 15, "ReviewAgent");
        collector.recordError("deepseek-chat", "provider_unavailable", "ReviewAgent");
        collector.recordError("deepseek-chat", "provider_unavailable", "ReviewAgent");

        assertEquals(25.0, registry.get("ai_model_tokens_total")
                .tag("model_name", "deepseek-chat")
                .tag("token_type", "total")
                .tag("agent_name", "ReviewAgent")
                .counter()
                .count());
        assertEquals(2.0, registry.get("ai_model_errors_total")
                .tag("model_name", "deepseek-chat")
                .tag("error_type", "provider_unavailable")
                .tag("agent_name", "ReviewAgent")
                .counter()
                .count());
        assertEquals(2, registry.getMeters().size());
        assertNoHighCardinalityTags();
    }

    @Test
    void recordPromptCharsAddsPerAgentCounter() {
        collector.recordPromptChars("deepseek-chat", 42, "CodeGenAgent");
        collector.recordPromptChars("deepseek-chat", 8, "CodeGenAgent");

        assertEquals(50.0, registry.get("ai_agent_prompt_chars_total")
                .tag("model_name", "deepseek-chat")
                .tag("agent_name", "CodeGenAgent")
                .counter()
                .count());
        assertNoHighCardinalityTags();
    }

    @Test
    void recordPromptCompositionAddsBucketCounters() {
        collector.recordPromptComposition(
                "deepseek-chat", 10, 20, 30, 2, "CodeGenAgent");
        collector.recordPromptComposition(
                "deepseek-chat", 1, 2, 3, 1, "CodeGenAgent");

        assertEquals(11.0, promptCounter("ai_agent_prompt_system_chars_total"));
        assertEquals(22.0, promptCounter("ai_agent_prompt_memory_chars_total"));
        assertEquals(33.0, promptCounter("ai_agent_prompt_user_chars_total"));
        assertEquals(3.0, promptCounter("ai_agent_prompt_memory_messages_total"));
        assertNoHighCardinalityTags();
    }

    private double promptCounter(String metricName) {
        return registry.get(metricName)
                .tag("model_name", "deepseek-chat")
                .tag("agent_name", "CodeGenAgent")
                .counter()
                .count();
    }

    private void assertNoHighCardinalityTags() {
        for (Meter meter : registry.getMeters()) {
            Set<String> tagKeys = meter.getId().getTags().stream()
                    .map(tag -> tag.getKey())
                    .collect(java.util.stream.Collectors.toSet());
            assertFalse(tagKeys.stream().anyMatch(FORBIDDEN_HIGH_CARDINALITY_TAGS::contains),
                    () -> "High-cardinality tag found on " + meter.getId());
        }
    }
}
