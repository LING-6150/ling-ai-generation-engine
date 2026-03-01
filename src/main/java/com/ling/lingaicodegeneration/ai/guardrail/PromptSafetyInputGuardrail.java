package com.ling.lingaicodegeneration.ai.guardrail;

import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.guardrail.InputGuardrail;
import dev.langchain4j.guardrail.InputGuardrailResult;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Input Guardrail — validates user prompts before sending to LLM.
 *
 * Blocks:
 * 1. Prompts that are too long (>1000 chars)
 * 2. Prompts containing sensitive keywords (prompt injection attempts)
 * 3. Prompts matching known injection patterns (regex-based)
 *
 * Note: This is basic rule-based detection. For production, consider
 * integrating with a dedicated content moderation API (e.g. Alibaba Cloud
 * Content Safety) for more sophisticated detection.
 */
@Slf4j
public class PromptSafetyInputGuardrail implements InputGuardrail {

    // Keywords that suggest prompt injection attempts
    private static final List<String> SENSITIVE_WORDS = Arrays.asList(
            "ignore previous instructions", "ignore above",
            "disregard", "hack", "bypass", "jailbreak",
            "forget your instructions", "you are now"
    );

    // Regex patterns for common injection techniques
    private static final List<Pattern> INJECTION_PATTERNS = Arrays.asList(
            Pattern.compile("(?i)(?:ignore|disregard)\\s+(?:previous|above|all)\\s+(?:instructions?|commands?|prompts?)"),
            Pattern.compile("(?i)(?:forget|disregard)\\s+(?:everything|all)\\s+(?:above|before)"),
            Pattern.compile("(?i)(?:pretend|act|behave)\\s+(?:as|like)\\s+(?:if|like)\\s+(?:you|i)\\s+(?:are|were)"),
            Pattern.compile("(?i)system\\s*:\\s*you\\s+are"),
            Pattern.compile("(?i)new\\s+(?:instructions?|commands?|prompts?)\\s*:")
    );

    @Override
    public InputGuardrailResult validate(UserMessage userMessage) {
        String input = userMessage.singleText();

        // 1. Check length
        if (input.length() > 1000) {
            return fatal("Input is too long, please keep it under 1000 characters");
        }

        // 2. Check for empty input
        if (input.trim().isEmpty()) {
            return fatal("Input cannot be empty");
        }

        // 3. Check sensitive keywords
        String lowerInput = input.toLowerCase();
        for (String sensitiveWord : SENSITIVE_WORDS) {
            if (lowerInput.contains(sensitiveWord.toLowerCase())) {
                log.warn("Guardrails blocked sensitive keyword: {}", sensitiveWord);
                return fatal("Input contains inappropriate content, please rephrase");
            }
        }

        // 4. Check injection patterns
        for (Pattern pattern : INJECTION_PATTERNS) {
            if (pattern.matcher(input).find()) {
                log.warn("Guardrails blocked injection pattern: {}", pattern.pattern());
                return fatal("Injection attempt detected, request blocked");
            }
        }

        return success();
    }
}