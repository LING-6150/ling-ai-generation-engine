package com.ling.lingaicodegeneration.ai;

import com.ling.lingaicodegeneration.ai.langgraph4j.model.QualityResult;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

/**
 * AI service for code quality checking
 * Returns structured QualityResult
 */
public interface CodeQualityCheckService {

    @SystemMessage(fromResource = "prompt/code-quality-check-system-prompt.txt")
    QualityResult checkCodeQuality(@UserMessage String codeContent);
}