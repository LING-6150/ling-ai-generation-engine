package com.ling.lingaicodegeneration.service.impl;

import com.ling.lingaicodegeneration.model.entity.ChatHistory;
import com.mybatisflex.core.query.QueryWrapper;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class ChatHistoryServiceImplTest {

    @Test
    void loadChatHistoryToMemoryClearsMemoryWhenMysqlHistoryIsEmpty() {
        ChatHistoryServiceImpl service = spy(new ChatHistoryServiceImpl());
        MessageWindowChatMemory chatMemory = mock(MessageWindowChatMemory.class);
        doReturn(Collections.emptyList()).when(service).list(any(QueryWrapper.class));

        int loaded = service.loadChatHistoryToMemory(123L, chatMemory, 20);

        assertEquals(0, loaded);
        verify(chatMemory).clear();
        verify(chatMemory, never()).add(any(ChatMessage.class));
    }

    @Test
    void loadChatHistoryToMemoryClearsBeforeLoadingHistoryInChronologicalOrder() {
        ChatHistoryServiceImpl service = spy(new ChatHistoryServiceImpl());
        MessageWindowChatMemory chatMemory = mock(MessageWindowChatMemory.class);
        doReturn(new ArrayList<>(List.of(history("newest"), history("oldest"))))
                .when(service).list(any(QueryWrapper.class));

        int loaded = service.loadChatHistoryToMemory(123L, chatMemory, 20);

        assertEquals(2, loaded);
        InOrder order = inOrder(chatMemory);
        order.verify(chatMemory).clear();

        ArgumentCaptor<ChatMessage> messageCaptor = ArgumentCaptor.forClass(ChatMessage.class);
        order.verify(chatMemory, times(2)).add(messageCaptor.capture());

        List<ChatMessage> messages = messageCaptor.getAllValues();
        assertEquals("oldest", ((UserMessage) messages.get(0)).singleText());
        assertEquals("newest", ((UserMessage) messages.get(1)).singleText());
    }

    private ChatHistory history(String message) {
        return ChatHistory.builder()
                .appId(123L)
                .message(message)
                .messageType("user")
                .build();
    }
}
