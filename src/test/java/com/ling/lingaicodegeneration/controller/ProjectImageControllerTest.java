package com.ling.lingaicodegeneration.controller;

import com.ling.lingaicodegeneration.exception.BusinessException;
import com.ling.lingaicodegeneration.model.entity.App;
import com.ling.lingaicodegeneration.model.entity.User;
import com.ling.lingaicodegeneration.model.enums.CodeGenTypeEnum;
import com.ling.lingaicodegeneration.service.AppService;
import com.ling.lingaicodegeneration.service.KreaImageService;
import com.ling.lingaicodegeneration.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProjectImageControllerTest {
    @Test
    void ownerReceivesAssetPathAndOtherUsersCannotStartInference() {
        AppService apps = mock(AppService.class);
        UserService users = mock(UserService.class);
        KreaImageService images = mock(KreaImageService.class);
        HttpServletRequest request = mock(HttpServletRequest.class);
        var controller = new ProjectImageController(apps, users, images);
        App app = new App();
        app.setUserId(17L);
        app.setCodeGenType("html");
        when(apps.getById(9L)).thenReturn(app);
        when(users.getLoginUser(request)).thenReturn(User.builder().id(3L).build(), User.builder().id(17L).build());
        var body = new ProjectImageController.ImageRequest("a leaf");
        assertThrows(BusinessException.class, () -> controller.generate(9L, body, request));
        verifyNoInteractions(images);
        when(images.generate(9L, CodeGenTypeEnum.HTML, "a leaf")).thenReturn("assets/leaf.png");
        assertEquals("assets/leaf.png", controller.generate(9L, body, request).getData().path());
    }
}
