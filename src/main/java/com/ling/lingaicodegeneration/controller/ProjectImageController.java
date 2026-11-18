package com.ling.lingaicodegeneration.controller;

import com.ling.lingaicodegeneration.common.BaseResponse;
import com.ling.lingaicodegeneration.common.ResultUtils;
import com.ling.lingaicodegeneration.exception.ErrorCode;
import com.ling.lingaicodegeneration.exception.ThrowUtils;
import com.ling.lingaicodegeneration.model.entity.App;
import com.ling.lingaicodegeneration.model.entity.User;
import com.ling.lingaicodegeneration.model.enums.CodeGenTypeEnum;
import com.ling.lingaicodegeneration.ratelimit.annotation.RateLimit;
import com.ling.lingaicodegeneration.ratelimit.enums.RateLimitType;
import com.ling.lingaicodegeneration.service.AppService;
import com.ling.lingaicodegeneration.service.KreaImageService;
import com.ling.lingaicodegeneration.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.images.krea.enabled", havingValue = "true")
public class ProjectImageController {
    private final AppService apps;
    private final UserService users;
    private final KreaImageService images;

    public record ImageRequest(String prompt) {}
    public record ImageAsset(String path) {}

    @PostMapping("/app/{appId}/images")
    @RateLimit(key = "project-image", limitType = RateLimitType.USER, rate = 3, rateInterval = 60)
    public BaseResponse<ImageAsset> generate(@PathVariable long appId, @RequestBody ImageRequest body,
                                             HttpServletRequest request) {
        User owner = users.getLoginUser(request);
        App app = apps.getById(appId);
        ThrowUtils.throwIf(app == null, ErrorCode.NOT_FOUND_ERROR);
        ThrowUtils.throwIf(!owner.getId().equals(app.getUserId()), ErrorCode.NO_AUTH_ERROR);
        CodeGenTypeEnum type = CodeGenTypeEnum.getEnumByValue(app.getCodeGenType());
        ThrowUtils.throwIf(type == null || body == null, ErrorCode.PARAMS_ERROR);
        return ResultUtils.success(new ImageAsset(images.generate(appId, type, body.prompt())));
    }
}
