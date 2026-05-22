package com.ling.lingaicodegeneration.multiagent.config;

import dev.langchain4j.model.chat.Capability;
import dev.langchain4j.model.openai.OpenAiChatModel;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Verifies LangChain4j 1.1.0 behavior:
 * RESPONSE_FORMAT_JSON_SCHEMA capability is only registered when
 * OpenAiChatModel.builder().responseFormat("json_schema") is set.
 *
 * Evidence from bytecode (OpenAiChatModel.supportedCapabilities):
 *   if ("json_schema".equals(this.responseFormat)) {
 *       capabilities.add(Capability.RESPONSE_FORMAT_JSON_SCHEMA);
 *   }
 *
 * Without this capability, DefaultAiServices falls back to prompt-injection
 * mode, which is less reliable for nested POJOs like ReviewReport.
 */
class ReviewModelCapabilityVerificationTest {

    private static final String DUMMY_KEY = "test-key";
    private static final String MODEL = "gpt-4o-mini";

    @Test
    void withoutResponseFormat_doesNotAdvertiseJsonSchemaCap() {
        // PRE-FIX state: ReviewModelConfig had no .responseFormat(...)
        OpenAiChatModel model = OpenAiChatModel.builder()
                .apiKey(DUMMY_KEY)
                .modelName(MODEL)
                .build();

        assertFalse(
                model.supportedCapabilities().contains(Capability.RESPONSE_FORMAT_JSON_SCHEMA),
                "Without responseFormat setting, RESPONSE_FORMAT_JSON_SCHEMA must NOT be advertised"
        );
    }

    @Test
    void withResponseFormatJsonObject_doesNotAdvertiseJsonSchemaCap() {
        // json_object mode (used by structuredRoutingChatModel) ≠ json_schema
        OpenAiChatModel model = OpenAiChatModel.builder()
                .apiKey(DUMMY_KEY)
                .modelName(MODEL)
                .responseFormat("json_object")
                .build();

        assertFalse(
                model.supportedCapabilities().contains(Capability.RESPONSE_FORMAT_JSON_SCHEMA),
                "json_object mode does NOT activate RESPONSE_FORMAT_JSON_SCHEMA capability"
        );
    }

    @Test
    void withResponseFormatJsonSchema_advertisesJsonSchemaCap() {
        // POST-FIX state: ReviewModelConfig adds .responseFormat("json_schema")
        OpenAiChatModel model = OpenAiChatModel.builder()
                .apiKey(DUMMY_KEY)
                .modelName(MODEL)
                .responseFormat("json_schema")
                .build();

        assertTrue(
                model.supportedCapabilities().contains(Capability.RESPONSE_FORMAT_JSON_SCHEMA),
                "responseFormat(\"json_schema\") MUST activate RESPONSE_FORMAT_JSON_SCHEMA capability"
        );
    }
}
