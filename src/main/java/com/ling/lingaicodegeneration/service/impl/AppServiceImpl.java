package com.ling.lingaicodegeneration.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.ling.lingaicodegeneration.constant.AppConstant;
import com.ling.lingaicodegeneration.core.AiCodeGeneratorFacade;
import com.ling.lingaicodegeneration.core.builder.VueProjectBuilder;
import com.ling.lingaicodegeneration.core.handler.StreamHandlerExecutor;
import com.ling.lingaicodegeneration.exception.BusinessException;
import com.ling.lingaicodegeneration.exception.ErrorCode;
import com.ling.lingaicodegeneration.exception.ThrowUtils;
import com.ling.lingaicodegeneration.model.dto.app.AppQueryRequest;
import com.ling.lingaicodegeneration.model.entity.App;
import com.ling.lingaicodegeneration.model.entity.User;
import com.ling.lingaicodegeneration.model.enums.ChatHistoryMessageTypeEnum;
import com.ling.lingaicodegeneration.model.enums.CodeGenTypeEnum;
import com.ling.lingaicodegeneration.model.vo.AppVO;
import com.ling.lingaicodegeneration.model.vo.UserVO;
import com.ling.lingaicodegeneration.mapper.AppMapper;
import com.ling.lingaicodegeneration.service.AppService;
import com.ling.lingaicodegeneration.service.ChatHistoryService;
import com.ling.lingaicodegeneration.service.UserService;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;


import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.ling.lingaicodegeneration.model.entity.table.AppTableDef.APP;

@Slf4j
@Service
public class AppServiceImpl extends ServiceImpl<AppMapper, App> implements AppService {

    @Resource
    private UserService userService;

    @Resource
    private AiCodeGeneratorFacade aiCodeGeneratorFacade;

    @Resource
    private ChatHistoryService chatHistoryService;

    @Override
    public QueryWrapper getQueryWrapper(AppQueryRequest appQueryRequest) {
        if (appQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Request params cannot be null");
        }
        Long id = appQueryRequest.getId();
        String appName = appQueryRequest.getAppName();
        String cover = appQueryRequest.getCover();
        String initPrompt = appQueryRequest.getInitPrompt();
        String codeGenType = appQueryRequest.getCodeGenType();
        String deployKey = appQueryRequest.getDeployKey();
        Integer priority = appQueryRequest.getPriority();
        Long userId = appQueryRequest.getUserId();
        String sortField = appQueryRequest.getSortField();
        String sortOrder = appQueryRequest.getSortOrder();
        return QueryWrapper.create()
                .eq("id", id)
                .like("appName", appName)
                .like("cover", cover)
                .like("initPrompt", initPrompt)
                .eq("codeGenType", codeGenType)
                .eq("deployKey", deployKey)
                .eq("priority", priority)
                .eq("userId", userId)
                .orderBy(sortField, "ascend".equals(sortOrder));
    }

    @Override
    public AppVO getAppVO(App app) {
        if (app == null) {
            return null;
        }
        AppVO appVO = new AppVO();
        BeanUtils.copyProperties(app, appVO);
        Long userId = app.getUserId();
        if (userId != null) {
            User user = userService.getById(userId);
            UserVO userVO = userService.getUserVO(user);
            appVO.setUser(userVO);
        }
        return appVO;
    }

    @Override
    public List<AppVO> getAppVOList(List<App> appList) {
        if (appList == null || appList.isEmpty()) {
            return new ArrayList<>();
        }
        // Batch fetch user info, avoid N+1 query
        Set<Long> userIds = appList.stream()
                .map(App::getUserId)
                .collect(Collectors.toSet());
        Map<Long, UserVO> userVOMap = userService.listByIds(userIds).stream()
                .collect(Collectors.toMap(User::getId, userService::getUserVO));
        return appList.stream().map(app -> {
            AppVO appVO = getAppVO(app);
            UserVO userVO = userVOMap.get(app.getUserId());
            appVO.setUser(userVO);
            return appVO;
        }).collect(Collectors.toList());
    }

    @Resource
    private VueProjectBuilder vueProjectBuilder;

    @Override
    public String deployApp(Long appId, User loginUser) {
        // 1. Validate params
        if (appId == null || appId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "App ID cannot be null");
        }
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        // 2. Get app info
        App app = this.getById(appId);
        if (app == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "App not found");
        }
        // 3. Check permission - only owner can deploy
        if (!app.getUserId().equals(loginUser.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "No permission to deploy this app");
        }
        // 4. Check if deployKey exists
        String deployKey = app.getDeployKey();
        if (StrUtil.isBlank(deployKey)) {
            deployKey = RandomUtil.randomString(6);
        }
        // 5. Get code gen type and source dir
        String codeGenType = app.getCodeGenType();
        String sourceDirName = codeGenType + "_" + appId;
        String sourceDirPath = AppConstant.CODE_OUTPUT_ROOT_DIR + File.separator + sourceDirName;
        // 6. Check if source dir exists
        File sourceDir = new File(sourceDirPath);
        if (!sourceDir.exists() || !sourceDir.isDirectory()) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "App code not found, please generate first");
        }
        // 7. Vue 项目特殊处理：需要构建后使用 dist 目录
        CodeGenTypeEnum codeGenTypeEnum = CodeGenTypeEnum.getEnumByValue(codeGenType);
        if (codeGenTypeEnum == CodeGenTypeEnum.VUE_PROJECT) {
            // 执行构建
            boolean buildSuccess = vueProjectBuilder.buildProject(sourceDirPath);
            ThrowUtils.throwIf(!buildSuccess, ErrorCode.SYSTEM_ERROR,
                    "Vue project build failed, please check the generated code");
            // 检查 dist 目录是否存在
            File distDir = new File(sourceDir, "dist");
            ThrowUtils.throwIf(!distDir.exists(), ErrorCode.SYSTEM_ERROR,
                    "Vue project build completed but dist directory not found");
            // 将 dist 目录作为部署源
            sourceDir = distDir;
            log.info("Vue project built successfully, deploying dist directory: {}",
                    distDir.getAbsolutePath());
        }
        // 8. Copy files to deploy directory
        String deployDirPath = AppConstant.CODE_DEPLOY_ROOT_DIR + File.separator + deployKey;
        try {
            FileUtils.copyDirectory(sourceDir, new File(deployDirPath));
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Deploy failed: " + e.getMessage());
        }
        // 9. Update deployKey and deployedTime
        App updateApp = new App();
        updateApp.setId(appId);
        updateApp.setDeployKey(deployKey);
        updateApp.setDeployedTime(java.time.LocalDateTime.now());
        boolean updateResult = this.updateById(updateApp);
        if (!updateResult) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "Failed to update deploy info");
        }
        // 10. Return URL
        return String.format("%s/%s", AppConstant.CODE_DEPLOY_HOST, deployKey);
    }



    @Override
    public boolean removeById(Serializable id) {
        if (id == null) {
            return false;
        }
        Long appId = Long.valueOf(id.toString());
        if (appId <= 0) {
            return false;
        }
        // 先删除关联的对话历史
        try {
            chatHistoryService.deleteByAppId(appId);
        } catch (Exception e) {
            // 记录日志但不阻止应用删除
            log.error("Failed to delete chat history for app: {}", e.getMessage());
        }
        // 删除应用
        return super.removeById(id);
    }

    @Resource
    private StreamHandlerExecutor streamHandlerExecutor;

    @Override
    public Flux<String> chatToGenCode(Long appId, String message, User loginUser) {
        // 1. Validate params
        if (appId == null || appId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "App ID cannot be null");
        }
        if (StrUtil.isBlank(message)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Message cannot be null");
        }
        // 2. Get app info
        App app = this.getById(appId);
        if (app == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "App not found");
        }
        // 3. Check permission
        if (!app.getUserId().equals(loginUser.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "No permission to access this app");
        }
        // 4. Get code gen type
        String codeGenTypeStr = app.getCodeGenType();
        CodeGenTypeEnum codeGenTypeEnum = CodeGenTypeEnum.getEnumByValue(codeGenTypeStr);
        if (codeGenTypeEnum == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Unsupported code gen type");
        }
        log.info("appId: {}, codeGenType from DB: {}, enum: {}", appId, codeGenTypeStr, codeGenTypeEnum);
        // 5. 保存用户消息到对话历史
        chatHistoryService.addChatMessage(appId, message,
                ChatHistoryMessageTypeEnum.USER.getValue(), loginUser.getId());

        // 6. Call AI to generate code (streaming)
        Flux<String> codeStream = aiCodeGeneratorFacade.generateAndSaveCodeStream(
                message, codeGenTypeEnum, appId);

        // 7. 使用流处理器执行器处理流，自动根据生成类型调用不同的处理器
        return streamHandlerExecutor.doExecute(codeStream, chatHistoryService,
                appId, loginUser, codeGenTypeEnum);
    }
}