package com.ling.lingaicodegeneration.multiagent.config;

import dev.langchain4j.model.chat.Capability;
import dev.langchain4j.model.chat.ChatModel;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Spring-context test: verifies the actual reviewChatModel bean advertises
 * RESPONSE_FORMAT_JSON_SCHEMA capability after ReviewModelConfig was updated.
 *
 * PRE-FIX:  this test fails (capability not present → DefaultAiServices
 *           falls back to prompt-injection mode for ReviewReport parsing)
 * POST-FIX: this test passes (capability present → JSON Schema Structured
 *           Outputs activated, reliable nested POJO deserialization)
 */
@SpringBootTest
class ReviewModelBeanCapabilityTest {

    @Resource(name = "reviewChatModel")
    private ChatModel reviewChatModel;

    @Test
    void reviewChatModelBean_mustAdvertiseJsonSchemaCapability() {
        assertTrue(
                reviewChatModel.supportedCapabilities()
                        .contains(Capability.RESPONSE_FORMAT_JSON_SCHEMA),
                "reviewChatModel bean must advertise RESPONSE_FORMAT_JSON_SCHEMA. " +
                "If this fails, check ReviewModelConfig: .responseFormat(\"json_schema\") is missing."
        );
    }
}
