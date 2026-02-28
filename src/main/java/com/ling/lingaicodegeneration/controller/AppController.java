package com.ling.lingaicodegeneration.controller;

import com.ling.lingaicodegeneration.annotation.AuthCheck;
import com.ling.lingaicodegeneration.common.BaseResponse;
import com.ling.lingaicodegeneration.common.DeleteRequest;
import com.ling.lingaicodegeneration.common.ResultUtils;
import com.ling.lingaicodegeneration.constant.AppConstant;
import com.ling.lingaicodegeneration.constant.UserConstant;
import com.ling.lingaicodegeneration.exception.BusinessException;
import com.ling.lingaicodegeneration.exception.ErrorCode;
import com.ling.lingaicodegeneration.exception.ThrowUtils;
import com.ling.lingaicodegeneration.model.dto.app.*;
import com.ling.lingaicodegeneration.model.entity.App;
import com.ling.lingaicodegeneration.model.entity.User;
import com.ling.lingaicodegeneration.model.enums.CodeGenTypeEnum;
import com.ling.lingaicodegeneration.model.vo.AppVO;
import com.ling.lingaicodegeneration.service.AppService;
import com.ling.lingaicodegeneration.ai.AiCodeGenTypeRoutingService;
import com.ling.lingaicodegeneration.service.ProjectDownloadService;
import com.ling.lingaicodegeneration.service.UserService;
import com.mybatisflex.core.query.QueryWrapper;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.io.File;
import java.util.List;

@RestController
@RequestMapping("/app")
@Slf4j
public class AppController {

    @Resource
    private AppService appService;

    @Resource
    private UserService userService;

    @Resource
    private ProjectDownloadService projectDownloadService;

    @Resource
    private AiCodeGenTypeRoutingService aiCodeGenTypeRoutingService;

    // ========== User APIs ==========

    /**
     * Create app
     */
    @PostMapping("/add")
    public BaseResponse<Long> addApp(@RequestBody AppAddRequest appAddRequest,
                                     HttpServletRequest request) {
        ThrowUtils.throwIf(appAddRequest == null, ErrorCode.PARAMS_ERROR);
        String initPrompt = appAddRequest.getInitPrompt();
        ThrowUtils.throwIf(initPrompt == null || initPrompt.isBlank(), ErrorCode.PARAMS_ERROR, "Initial prompt cannot be empty");
        User loginUser = userService.getLoginUser(request);
        App app = new App();
        BeanUtils.copyProperties(appAddRequest, app);
        app.setUserId(loginUser.getId());
        // Use first 12 chars of prompt as app name
        app.setAppName(initPrompt.substring(0, Math.min(initPrompt.length(), 12)));
        // Use AI routing to select the best code generation type
        // If user explicitly specified a type, respect it; otherwise use AI to decide
        String codeGenType = appAddRequest.getCodeGenType();
        if (codeGenType == null || codeGenType.isBlank()) {
            try {
                CodeGenTypeEnum routedType = aiCodeGenTypeRoutingService.routeCodeGenType(initPrompt);
                codeGenType = (routedType != null) ? routedType.getValue() : CodeGenTypeEnum.MULTI_FILE.getValue();
                log.info("AI routing selected: {} for prompt: {}", codeGenType, initPrompt.substring(0, Math.min(initPrompt.length(), 50)));
            } catch (Exception e) {
                log.warn("AI routing failed, falling back to MULTI_FILE: {}", e.getMessage());
                codeGenType = CodeGenTypeEnum.MULTI_FILE.getValue();
            }
        }
        app.setCodeGenType(codeGenType);
        boolean result = appService.save(app);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(app.getId());
    }

    /**
     * Update app (user can only update app name)
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updateApp(@RequestBody AppUpdateRequest appUpdateRequest,
                                           HttpServletRequest request) {
        ThrowUtils.throwIf(appUpdateRequest == null || appUpdateRequest.getId() == null,
                ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        long id = appUpdateRequest.getId();
        App oldApp = appService.getById(id);
        ThrowUtils.throwIf(oldApp == null, ErrorCode.NOT_FOUND_ERROR);
        if (!oldApp.getUserId().equals(loginUser.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        App app = new App();
        app.setId(id);
        app.setAppName(appUpdateRequest.getAppName());
        app.setEditTime(java.time.LocalDateTime.now());
        boolean result = appService.updateById(app);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * Delete app (user can only delete own app)
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteApp(@RequestBody DeleteRequest deleteRequest,
                                           HttpServletRequest request) {
        ThrowUtils.throwIf(deleteRequest == null || deleteRequest.getId() <= 0,
                ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        App oldApp = appService.getById(id);
        ThrowUtils.throwIf(oldApp == null, ErrorCode.NOT_FOUND_ERROR);
        if (!oldApp.getUserId().equals(loginUser.getId()) &&
                !UserConstant.ADMIN_ROLE.equals(loginUser.getUserRole())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean result = appService.removeById(id);
        return ResultUtils.success(result);
    }

    /**
     * Get app VO by id
     */
    @GetMapping("/get/vo")
    public BaseResponse<AppVO> getAppVOById(long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        App app = appService.getById(id);
        ThrowUtils.throwIf(app == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(appService.getAppVO(app));
    }

    /**
     * List my apps by page
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<com.mybatisflex.core.paginate.Page<AppVO>> listMyAppVOByPage(
            @RequestBody AppQueryRequest appQueryRequest,
            HttpServletRequest request) {
        ThrowUtils.throwIf(appQueryRequest == null, ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        long pageSize = appQueryRequest.getPageSize();
        ThrowUtils.throwIf(pageSize > 20, ErrorCode.PARAMS_ERROR, "Page size cannot exceed 20");
        long pageNum = appQueryRequest.getPageNum();
        appQueryRequest.setUserId(loginUser.getId());
        QueryWrapper queryWrapper = appService.getQueryWrapper(appQueryRequest);
        com.mybatisflex.core.paginate.Page<App> appPage = appService.page(
                new com.mybatisflex.core.paginate.Page<>(pageNum, pageSize), queryWrapper);
        com.mybatisflex.core.paginate.Page<AppVO> appVOPage =
                new com.mybatisflex.core.paginate.Page<>(pageNum, pageSize, appPage.getTotalRow());
        List<AppVO> appVOList = appService.getAppVOList(appPage.getRecords());
        appVOPage.setRecords(appVOList);
        return ResultUtils.success(appVOPage);
    }

    /**
     * List good apps by page
     */
    @PostMapping("/good/list/page/vo")
    public BaseResponse<com.mybatisflex.core.paginate.Page<AppVO>> listGoodAppVOByPage(
            @RequestBody AppQueryRequest appQueryRequest) {
        ThrowUtils.throwIf(appQueryRequest == null, ErrorCode.PARAMS_ERROR);
        long pageSize = appQueryRequest.getPageSize();
        ThrowUtils.throwIf(pageSize > 20, ErrorCode.PARAMS_ERROR, "Page size cannot exceed 20");
        long pageNum = appQueryRequest.getPageNum();
        appQueryRequest.setPriority(AppConstant.GOOD_APP_PRIORITY);
        QueryWrapper queryWrapper = appService.getQueryWrapper(appQueryRequest);
        com.mybatisflex.core.paginate.Page<App> appPage = appService.page(
                new com.mybatisflex.core.paginate.Page<>(pageNum, pageSize), queryWrapper);
        com.mybatisflex.core.paginate.Page<AppVO> appVOPage =
                new com.mybatisflex.core.paginate.Page<>(pageNum, pageSize, appPage.getTotalRow());
        List<AppVO> appVOList = appService.getAppVOList(appPage.getRecords());
        appVOPage.setRecords(appVOList);
        return ResultUtils.success(appVOPage);
    }

    /**
     * Deploy app
     */
    @PostMapping("/deploy")
    public BaseResponse<String> deployApp(@RequestBody AppDeployRequest appDeployRequest,
                                          HttpServletRequest request) {
        ThrowUtils.throwIf(appDeployRequest == null, ErrorCode.PARAMS_ERROR);
        Long appId = appDeployRequest.getAppId();
        ThrowUtils.throwIf(appId == null || appId <= 0, ErrorCode.PARAMS_ERROR, "App ID cannot be null");
        User loginUser = userService.getLoginUser(request);
        String deployUrl = appService.deployApp(appId, loginUser);
        return ResultUtils.success(deployUrl);
    }

    /**
     * Download app source code as ZIP.
     * Only the app owner can download.
     */
    @GetMapping("/download/{appId}")
    public void downloadAppCode(@PathVariable Long appId,
                                HttpServletRequest request,
                                HttpServletResponse response) {
        // 1. Validate params
        ThrowUtils.throwIf(appId == null || appId <= 0, ErrorCode.PARAMS_ERROR, "App ID is invalid");
        // 2. Get app info
        App app = appService.getById(appId);
        ThrowUtils.throwIf(app == null, ErrorCode.NOT_FOUND_ERROR, "App not found");
        // 3. Permission check: only the owner can download
        User loginUser = userService.getLoginUser(request);
        if (!app.getUserId().equals(loginUser.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "No permission to download this app's code");
        }
        // 4. Build source directory path (original source, not deploy dir)
        String codeGenType = app.getCodeGenType();
        String sourceDirName = codeGenType + "_" + appId;
        String sourceDirPath = AppConstant.CODE_OUTPUT_ROOT_DIR + File.separator + sourceDirName;
        // 5. Check source directory exists
        File sourceDir = new File(sourceDirPath);
        ThrowUtils.throwIf(!sourceDir.exists() || !sourceDir.isDirectory(),
                ErrorCode.NOT_FOUND_ERROR, "App code not found, please generate code first");
        // 6. Use appId as file name to avoid Chinese encoding issues in browser
        String downloadFileName = String.valueOf(appId);
        // 7. Stream ZIP to client
        log.info("User {} downloading app {} code", loginUser.getId(), appId);
        projectDownloadService.downloadProjectAsZip(sourceDirPath, downloadFileName, response);
    }

    /**
     * Chat to generate code (SSE streaming)
     */
    @GetMapping(value = "/chat/gen/code", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> chatToGenCode(@RequestParam Long appId,
                                                       @RequestParam String message,
                                                       HttpServletRequest request) {
        ThrowUtils.throwIf(appId == null || appId <= 0, ErrorCode.PARAMS_ERROR, "App ID cannot be null");
        ThrowUtils.throwIf(message == null || message.isBlank(), ErrorCode.PARAMS_ERROR, "Message cannot be empty");
        User loginUser = userService.getLoginUser(request);
        Flux<String> contentFlux = appService.chatToGenCode(appId, message, loginUser);
        return contentFlux
                .map(chunk -> {
                    java.util.Map<String, String> wrapper = java.util.Map.of("d", chunk);
                    String jsonData = "";
                    try {
                        jsonData = new com.fasterxml.jackson.databind.ObjectMapper()
                                .writeValueAsString(wrapper);
                    } catch (Exception e) {
                        log.error("JSON serialization error: {}", e.getMessage());
                    }
                    return ServerSentEvent.<String>builder()
                            .data(jsonData)
                            .build();
                })
                .concatWith(reactor.core.publisher.Mono.just(
                        ServerSentEvent.<String>builder()
                                .event("done")
                                .data("")
                                .build()
                ));
    }

    // ========== Admin APIs ==========

    /**
     * Admin delete app
     */
    @PostMapping("/admin/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteAppByAdmin(@RequestBody DeleteRequest deleteRequest) {
        ThrowUtils.throwIf(deleteRequest == null || deleteRequest.getId() <= 0,
                ErrorCode.PARAMS_ERROR);
        long id = deleteRequest.getId();
        App oldApp = appService.getById(id);
        ThrowUtils.throwIf(oldApp == null, ErrorCode.NOT_FOUND_ERROR);
        boolean result = appService.removeById(id);
        return ResultUtils.success(result);
    }

    /**
     * Admin update app
     */
    @PostMapping("/admin/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateAppByAdmin(@RequestBody AppAdminUpdateRequest appAdminUpdateRequest) {
        ThrowUtils.throwIf(appAdminUpdateRequest == null || appAdminUpdateRequest.getId() == null,
                ErrorCode.PARAMS_ERROR);
        long id = appAdminUpdateRequest.getId();
        App oldApp = appService.getById(id);
        ThrowUtils.throwIf(oldApp == null, ErrorCode.NOT_FOUND_ERROR);
        App app = new App();
        BeanUtils.copyProperties(appAdminUpdateRequest, app);
        app.setEditTime(java.time.LocalDateTime.now());
        boolean result = appService.updateById(app);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * Admin list apps by page
     */
    @PostMapping("/admin/list/page/vo")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<com.mybatisflex.core.paginate.Page<AppVO>> listAppVOByPageAdmin(
            @RequestBody AppQueryRequest appQueryRequest) {
        ThrowUtils.throwIf(appQueryRequest == null, ErrorCode.PARAMS_ERROR);
        long pageNum = appQueryRequest.getPageNum();
        long pageSize = appQueryRequest.getPageSize();
        QueryWrapper queryWrapper = appService.getQueryWrapper(appQueryRequest);
        com.mybatisflex.core.paginate.Page<App> appPage = appService.page(
                new com.mybatisflex.core.paginate.Page<>(pageNum, pageSize), queryWrapper);
        com.mybatisflex.core.paginate.Page<AppVO> appVOPage =
                new com.mybatisflex.core.paginate.Page<>(pageNum, pageSize, appPage.getTotalRow());
        List<AppVO> appVOList = appService.getAppVOList(appPage.getRecords());
        appVOPage.setRecords(appVOList);
        return ResultUtils.success(appVOPage);
    }

    /**
     * Admin get app VO by id
     */
    @GetMapping("/admin/get/vo")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<AppVO> getAppVOByIdAdmin(long id) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        App app = appService.getById(id);
        ThrowUtils.throwIf(app == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(appService.getAppVO(app));
    }
}