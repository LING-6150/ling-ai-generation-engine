package com.ling.lingaicodegeneration.controller;

import com.ling.lingaicodegeneration.ai.AiCodeGenTypeRoutingService;
import com.ling.lingaicodegeneration.model.entity.User;
import com.ling.lingaicodegeneration.service.AppService;
import com.ling.lingaicodegeneration.service.ProjectDownloadService;
import com.ling.lingaicodegeneration.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Flux;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AppControllerTest {

    @Mock
    private AppService appService;

    @Mock
    private UserService userService;

    @Mock
    private ProjectDownloadService projectDownloadService;

    @Mock
    private AiCodeGenTypeRoutingService aiCodeGenTypeRoutingService;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private AppController appController;

    @Test
    void chatToGenCodeEmitsWorkflowErrorWhenContentFluxIsEmpty() {
        User user = new User();
        user.setId(1L);
        when(userService.getLoginUser(request)).thenReturn(user);
        when(appService.chatToGenCode(eq(123L), eq("build app"), eq(user), eq(true), eq(true)))
                .thenReturn(Flux.empty());

        List<ServerSentEvent<String>> events = appController
                .chatToGenCode(123L, "build app", true, true, request)
                .collectList()
                .block();

        assertEquals(2, events.size());
        assertTrue(events.get(0).data().contains("workflow_error"));
        assertTrue(events.get(0).data().contains("Generation stream completed without events"));
        assertEquals("done", events.get(1).event());
    }

    @Test
    void chatToGenCodeKeepsExistingContentEvents() {
        User user = new User();
        user.setId(1L);
        when(userService.getLoginUser(request)).thenReturn(user);
        when(appService.chatToGenCode(eq(123L), eq("build app"), eq(user), eq(true), eq(false)))
                .thenReturn(Flux.just("{\"type\":\"code_token\",\"content\":\"hello\"}"));

        List<ServerSentEvent<String>> events = appController
                .chatToGenCode(123L, "build app", true, false, request)
                .collectList()
                .block();

        assertEquals(2, events.size());
        assertTrue(events.get(0).data().contains("code_token"));
        assertEquals("done", events.get(1).event());
    }
}
