package com.ling.lingaicodegeneration.monitor;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AiModelMetricsCollectorTest {

    @Test
    void recordWorkflowErrorAddsLowCardinalityCounter() {
        SimpleMeterRegistry registry = new SimpleMeterRegistry();
        AiModelMetricsCollector collector = new AiModelMetricsCollector();
        ReflectionTestUtils.setField(collector, "meterRegistry", registry);

        collector.recordWorkflowError(
                "user-1",
                "app-1",
                "OrchestratorAgent",
                "workflow_empty_stream",
                true
        );

        assertEquals(1.0, registry.get("ai_workflow_errors_total")
                .tag("user_id", "user-1")
                .tag("app_id", "app-1")
                .tag("agent_name", "OrchestratorAgent")
                .tag("error_type", "workflow_empty_stream")
                .tag("context_pruning", "true")
                .counter()
                .count());
    }

    @Test
    void recordPromptCharsAddsPerAgentCounter() {
        SimpleMeterRegistry registry = new SimpleMeterRegistry();
        AiModelMetricsCollector collector = new AiModelMetricsCollector();
        ReflectionTestUtils.setField(collector, "meterRegistry", registry);

        collector.recordPromptChars("user-1", "app-1", "deepseek-chat", 42, "CodeGenAgent");
        collector.recordPromptChars("user-1", "app-1", "deepseek-chat", 8, "CodeGenAgent");

        assertEquals(50.0, registry.get("ai_agent_prompt_chars_total")
                .tag("user_id", "user-1")
                .tag("app_id", "app-1")
                .tag("model_name", "deepseek-chat")
                .tag("agent_name", "CodeGenAgent")
                .counter()
                .count());
    }

    @Test
    void recordPromptCompositionAddsBucketCounters() {
        SimpleMeterRegistry registry = new SimpleMeterRegistry();
        AiModelMetricsCollector collector = new AiModelMetricsCollector();
        ReflectionTestUtils.setField(collector, "meterRegistry", registry);

        collector.recordPromptComposition(
                "user-1", "app-1", "deepseek-chat",
                10, 20, 30, 2, "CodeGenAgent");
        collector.recordPromptComposition(
                "user-1", "app-1", "deepseek-chat",
                1, 2, 3, 1, "CodeGenAgent");

        assertEquals(11.0, registry.get("ai_agent_prompt_system_chars_total")
                .tag("user_id", "user-1")
                .tag("app_id", "app-1")
                .tag("model_name", "deepseek-chat")
                .tag("agent_name", "CodeGenAgent")
                .counter()
                .count());
        assertEquals(22.0, registry.get("ai_agent_prompt_memory_chars_total")
                .tag("user_id", "user-1")
                .tag("app_id", "app-1")
                .tag("model_name", "deepseek-chat")
                .tag("agent_name", "CodeGenAgent")
                .counter()
                .count());
        assertEquals(33.0, registry.get("ai_agent_prompt_user_chars_total")
                .tag("user_id", "user-1")
                .tag("app_id", "app-1")
                .tag("model_name", "deepseek-chat")
                .tag("agent_name", "CodeGenAgent")
                .counter()
                .count());
        assertEquals(3.0, registry.get("ai_agent_prompt_memory_messages_total")
                .tag("user_id", "user-1")
                .tag("app_id", "app-1")
                .tag("model_name", "deepseek-chat")
                .tag("agent_name", "CodeGenAgent")
                .counter()
                .count());
    }
}
