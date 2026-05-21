package com.ling.lingaicodegeneration.ai.multiagent.llm;

import com.ling.lingaicodegeneration.ai.multiagent.model.TaskGraph;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

public interface PlannerLlmService {

    @SystemMessage(fromResource = "prompt/planning-system-prompt.txt")
    TaskGraph plan(@UserMessage String requirementSpecJson);
}
