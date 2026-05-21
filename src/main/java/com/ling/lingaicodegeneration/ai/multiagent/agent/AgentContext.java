package com.ling.lingaicodegeneration.ai.multiagent.agent;

public record AgentContext(
        Long appId,
        Long userId,
        int retryBudget,
        String agentName
) {
    public AgentContext withAgentName(String name) {
        return new AgentContext(appId, userId, retryBudget, name);
    }

    public AgentContext withRetryBudget(int budget) {
        return new AgentContext(appId, userId, budget, agentName);
    }
}
