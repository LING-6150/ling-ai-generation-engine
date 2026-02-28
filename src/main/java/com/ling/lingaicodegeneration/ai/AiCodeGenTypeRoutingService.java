package com.ling.lingaicodegeneration.ai;

import com.ling.lingaicodegeneration.model.enums.CodeGenTypeEnum;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

/**
 * AI code generation type routing service.
 * Uses structured output to return a CodeGenTypeEnum directly.
 */
public interface AiCodeGenTypeRoutingService {

    /**
     * Analyze user prompt and select the most appropriate code generation type.
     *
     * @param userPrompt user's app description
     * @return recommended CodeGenTypeEnum
     */
    @SystemMessage(fromResource = "prompt/codegen-routing-system-prompt.txt")
    CodeGenTypeEnum routeCodeGenType(@UserMessage String userPrompt);
}