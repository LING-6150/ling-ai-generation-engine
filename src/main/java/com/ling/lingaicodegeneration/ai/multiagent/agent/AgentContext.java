package com.ling.lingaicodegeneration.ai.multiagent.agent;

public record AgentContext(
        Long appId,
        Long userId,
        int retryBudget,
        String agentName,
        boolean contextPruningEnabled,
        boolean executionFeedbackEnabled
) {
    public AgentContext(Long appId, Long userId, int retryBudget, String agentName) {
        this(appId, userId, retryBudget, agentName, false, false);
    }

    public AgentContext(Long appId, Long userId, int retryBudget, String agentName,
                        boolean contextPruningEnabled) {
        this(appId, userId, retryBudget, agentName, contextPruningEnabled, false);
    }

    public AgentContext withAgentName(String name) {
        return new AgentContext(appId, userId, retryBudget, name, contextPruningEnabled, executionFeedbackEnabled);
    }

    public AgentContext withRetryBudget(int budget) {
        return new AgentContext(appId, userId, budget, agentName, contextPruningEnabled, executionFeedbackEnabled);
    }

    public AgentContext withContextPruningEnabled(boolean enabled) {
        return new AgentContext(appId, userId, retryBudget, agentName, enabled, executionFeedbackEnabled);
    }

    public AgentContext withExecutionFeedbackEnabled(boolean enabled) {
        return new AgentContext(appId, userId, retryBudget, agentName, contextPruningEnabled, enabled);
    }
}
