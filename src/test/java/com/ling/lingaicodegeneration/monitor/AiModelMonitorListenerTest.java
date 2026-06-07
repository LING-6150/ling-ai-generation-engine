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

    @Test
    void classifyPromptCompositionBucketsSystemMemoryAndCurrentUser() {
        List<ChatMessage> messages = List.of(
                SystemMessage.from("system"),
                UserMessage.from("old user"),
                AiMessage.from("old answer"),
                ToolExecutionResultMessage.from("id-1", "tool", "tool result"),
                UserMessage.from("current prompt")
        );

        AiModelMonitorListener.PromptComposition composition =
                AiModelMonitorListener.classifyPromptComposition(messages);

        assertEquals("system".length(), composition.systemChars());
        assertEquals("old userold answertool result".length(), composition.memoryChars());
        assertEquals("current prompt".length(), composition.userChars());
        assertEquals(3, composition.memoryMessages());
        assertEquals(AiModelMonitorListener.countPromptChars(messages), composition.totalChars());
    }

    @Test
    void classifyPromptCompositionTreatsEarlierUserMessagesAsMemory() {
        List<ChatMessage> messages = List.of(
                UserMessage.from("first"),
                UserMessage.from("second")
        );

        AiModelMonitorListener.PromptComposition composition =
                AiModelMonitorListener.classifyPromptComposition(messages);

        assertEquals(0, composition.systemChars());
        assertEquals("first".length(), composition.memoryChars());
        assertEquals("second".length(), composition.userChars());
        assertEquals(1, composition.memoryMessages());
    }
}
