package com.ling.lingaicodegeneration.ai.multiagent.llm;

import com.ling.lingaicodegeneration.ai.multiagent.model.RequirementSpec;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

public interface RequirementLlmService {

    @SystemMessage(fromResource = "prompt/requirement-analysis-system-prompt.txt")
    RequirementSpec analyze(@UserMessage String userPrompt);
}
