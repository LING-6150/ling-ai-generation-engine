package com.ling.lingaicodegeneration.controller;

import com.ling.lingaicodegeneration.aop.AuthInterceptor;
import com.ling.lingaicodegeneration.exception.BusinessException;
import com.ling.lingaicodegeneration.exception.ErrorCode;
import com.ling.lingaicodegeneration.exception.GlobalExceptionHandler;
import com.ling.lingaicodegeneration.model.entity.User;
import com.ling.lingaicodegeneration.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

class UserLookupTest {
    private final UserServiceImpl users = mock(UserServiceImpl.class);
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        UserController controller = new UserController();
        AuthInterceptor authorization = new AuthInterceptor();
        ReflectionTestUtils.setField(controller, "userService", users);
        ReflectionTestUtils.setField(authorization, "userService", users);
        AspectJProxyFactory factory = new AspectJProxyFactory(controller);
        factory.addAspect(authorization);
        UserController securedController = factory.getProxy();
        mvc = MockMvcBuilders.standaloneSetup(securedController)
                .setControllerAdvice(new GlobalExceptionHandler()).build();
    }

    @ParameterizedTest
    @ValueSource(strings = {"/user/get", "/user/get/vo"})
    void requiresAdminBeforeLoadingTheRecord(String path) throws Exception {
        when(users.getLoginUser(any()))
                .thenThrow(new BusinessException(ErrorCode.NOT_LOGIN_ERROR))
                .thenReturn(User.builder().userRole("user").build());
        mvc.perform(get(path).param("id", "7"))
                .andExpect(jsonPath("$.code").value(ErrorCode.NOT_LOGIN_ERROR.getCode()));
        mvc.perform(get(path).param("id", "7"))
                .andExpect(jsonPath("$.code").value(ErrorCode.NO_AUTH_ERROR.getCode()));
        verify(users, never()).getById(7L);
    }

    @ParameterizedTest
    @ValueSource(strings = {"/user/get", "/user/get/vo"})
    void returnsOnlyProfileFieldsToAdmins(String path) throws Exception {
        User record = User.builder().id(7L).userName("Test user").userPassword("test-hash").build();
        when(users.getLoginUser(any())).thenReturn(User.builder().userRole("admin").build());
        when(users.getById(7L)).thenReturn(record);
        when(users.getUserVO(record)).thenCallRealMethod();
        mvc.perform(get(path).param("id", "7"))
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.userName").value("Test user"))
                .andExpect(jsonPath("$.data.userPassword").doesNotHaveJsonPath());
    }
}
