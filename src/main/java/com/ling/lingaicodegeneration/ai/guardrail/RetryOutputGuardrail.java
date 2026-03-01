package com.ling.lingaicodegeneration.ai.guardrail;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.guardrail.OutputGuardrail;
import dev.langchain4j.guardrail.OutputGuardrailResult;
import lombok.extern.slf4j.Slf4j;

/**
 * Output Guardrail — validates LLM responses after generation.
 *
 * Checks:
 * 1. Response is not empty or too short
 * 2. Response does not contain sensitive information (API keys, secrets, etc.)
 *
 * When validation fails, returns reprompt() instead of fatal() —
 * LangChain4j will automatically re-send the request with additional context
 * asking the model to fix the issue, up to maxRetries times.
 *
 * ⚠️ Note: Output guardrails buffer the full response before validating,
 * so they have limited use with streaming responses. For streaming, apply
 * input guardrails instead and rely on content-level prompt constraints.
 * Only use output guardrails in non-streaming (batch) contexts for this project.
 */
@Slf4j
public class RetryOutputGuardrail implements OutputGuardrail {

    private static final String[] SENSITIVE_WORDS = {
            "password", "secret", "token", "api key", "api_key", "credential"
    };

    @Override
    public OutputGuardrailResult validate(AiMessage responseFromLLM) {
        String response = responseFromLLM.text();

        // 1. Check for empty or too-short response
        if (response == null || response.trim().isEmpty()) {
            return reprompt("Response is empty", "Please generate complete and detailed content");
        }
        if (response.trim().length() < 10) {
            return reprompt("Response is too short", "Please provide more detailed content");
        }

        // 2. Check for sensitive information leakage
        if (containsSensitiveContent(response)) {
            log.warn("Output guardrail detected sensitive content in response");
            return reprompt("Response contains sensitive information",
                    "Please regenerate content, avoid including sensitive information");
        }

        return success();
    }

    private boolean containsSensitiveContent(String response) {
        String lowerResponse = response.toLowerCase();
        for (String word : SENSITIVE_WORDS) {
            if (lowerResponse.contains(word)) {
                return true;
            }
        }
        return false;
    }
}