package com.ling.lingaicodegeneration.ai.multiagent.agent;

public record AgentContext(
        Long appId,
        Long userId,
        int retryBudget,
        String agentName,
        boolean contextPruningEnabled
) {
    public AgentContext(Long appId, Long userId, int retryBudget, String agentName) {
        this(appId, userId, retryBudget, agentName, false);
    }

    public AgentContext withAgentName(String name) {
        return new AgentContext(appId, userId, retryBudget, name, contextPruningEnabled);
    }

    public AgentContext withRetryBudget(int budget) {
        return new AgentContext(appId, userId, budget, agentName, contextPruningEnabled);
    }

    public AgentContext withContextPruningEnabled(boolean enabled) {
        return new AgentContext(appId, userId, retryBudget, agentName, enabled);
    }
}
