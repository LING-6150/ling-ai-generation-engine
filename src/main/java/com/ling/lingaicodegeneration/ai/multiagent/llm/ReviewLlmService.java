package com.ling.lingaicodegeneration.ai.multiagent.llm;

import com.ling.lingaicodegeneration.ai.multiagent.model.ReviewReport;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

public interface ReviewLlmService {

    @SystemMessage(fromResource = "prompt/review-agent-system-prompt.txt")
    ReviewReport review(@UserMessage String codeContextAndRequirements);
}
