package com.ling.lingaicodegeneration.ai.multiagent.agent;

import com.ling.lingaicodegeneration.ai.multiagent.llm.PlannerLlmService;
import com.ling.lingaicodegeneration.ai.multiagent.model.Complexity;
import com.ling.lingaicodegeneration.ai.multiagent.model.RequirementSpec;
import com.ling.lingaicodegeneration.ai.multiagent.model.TaskGraph;
import dev.langchain4j.internal.Json;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PlannerAgent implements Agent<RequirementSpec, TaskGraph> {

    @Resource
    private PlannerLlmService plannerLlmService;

    @Override
    public TaskGraph execute(RequirementSpec spec, AgentContext context) {
        log.info("PlannerAgent executing, appId: {}", context.appId());
        try {
            String specJson = Json.toJson(spec);
            TaskGraph plan = plannerLlmService.plan(specJson);
            int retries = Math.min(Math.max(plan.suggestedRetries(), 0), 3);
            TaskGraph validated = new TaskGraph(
                    plan.parallelizeAsset(),
                    retries,
                    plan.codeGenHints(),
                    plan.reviewFocusAreas()
            );
            log.info("PlannerAgent completed: parallelizeAsset={}, retries={}",
                    validated.parallelizeAsset(), validated.suggestedRetries());
            return validated;
        } catch (Exception e) {
            log.error("PlannerAgent failed, using sensible defaults: {}", e.getMessage());
            int defaultRetries = spec.getComplexity() == Complexity.COMPLEX ? 3
                    : spec.getComplexity() == Complexity.MEDIUM ? 2 : 1;
            return new TaskGraph(true, defaultRetries, null, null);
        }
    }
}
