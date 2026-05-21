package com.ling.lingaicodegeneration.ai.multiagent.agent;

@FunctionalInterface
public interface Agent<I, O> {
    O execute(I input, AgentContext context);
}
