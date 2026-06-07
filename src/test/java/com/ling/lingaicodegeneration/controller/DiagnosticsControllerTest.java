package com.ling.lingaicodegeneration.controller;

import com.ling.lingaicodegeneration.common.BaseResponse;
import com.ling.lingaicodegeneration.exception.BusinessException;
import dev.langchain4j.community.store.memory.chat.redis.RedisChatMemoryStore;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class DiagnosticsControllerTest {

    @Test
    void clearChatMemoryReturnsForbiddenWhenDiagnosticsDisabled() {
        RedisChatMemoryStore store = mock(RedisChatMemoryStore.class);
        DiagnosticsController controller = controller(store, false);

        BaseResponse<?> response = controller.clearChatMemory(123L);

        assertEquals(40300, response.getCode());
        verify(store, never()).deleteMessages(123L);
    }

    @Test
    void clearChatMemoryDeletesRedisMemoryWhenDiagnosticsEnabled() {
        RedisChatMemoryStore store = mock(RedisChatMemoryStore.class);
        DiagnosticsController controller = controller(store, true);

        BaseResponse<?> response = controller.clearChatMemory(123L);

        assertEquals(0, response.getCode());
        @SuppressWarnings("unchecked")
        var data = (java.util.Map<String, Object>) response.getData();
        assertEquals(123L, data.get("appId"));
        assertTrue((Boolean) data.get("redisChatMemoryCleared"));
        verify(store).deleteMessages(123L);
    }

    @Test
    void clearChatMemoryRejectsInvalidAppId() {
        RedisChatMemoryStore store = mock(RedisChatMemoryStore.class);
        DiagnosticsController controller = controller(store, true);

        assertThrows(BusinessException.class, () -> controller.clearChatMemory(0L));
        verify(store, never()).deleteMessages(0L);
    }

    private DiagnosticsController controller(RedisChatMemoryStore store, boolean enabled) {
        DiagnosticsController controller = new DiagnosticsController();
        ReflectionTestUtils.setField(controller, "redisChatMemoryStore", store);
        ReflectionTestUtils.setField(controller, "contextPruningDiagnosticsEnabled", enabled);
        return controller;
    }
}
