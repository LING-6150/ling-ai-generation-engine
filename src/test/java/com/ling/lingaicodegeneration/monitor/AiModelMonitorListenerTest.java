package com.ling.lingaicodegeneration.monitor;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.ToolExecutionResultMessage;
import dev.langchain4j.data.message.UserMessage;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AiModelMonitorListenerTest {

    @Test
    void countPromptCharsSumsSupportedMessageTypes() {
        List<ChatMessage> messages = List.of(
                SystemMessage.from("system"),
                UserMessage.from("hello"),
                AiMessage.from("answer"),
                ToolExecutionResultMessage.from("id-1", "tool", "result")
        );

        assertEquals("systemhelloanswerresult".length(),
                AiModelMonitorListener.countPromptChars(messages));
    }

    @Test
    void countPromptCharsHandlesNullAndEmptyMessages() {
        assertEquals(0, AiModelMonitorListener.countPromptChars(null));
        assertEquals(0, AiModelMonitorListener.countPromptChars(List.of()));
    }
}
