package com.ling.lingaicodegeneration.ai.multiagent.llm;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.UserMessage;

public interface RefineAiService {

    @SystemMessage(fromResource = "prompt/refine-agent-system-prompt.txt")
    TokenStream refine(@UserMessage String refinementRequest);
}
