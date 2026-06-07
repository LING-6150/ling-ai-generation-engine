package com.ling.lingaicodegeneration.controller;

import com.ling.lingaicodegeneration.common.BaseResponse;
import com.ling.lingaicodegeneration.common.ResultUtils;
import com.ling.lingaicodegeneration.exception.ErrorCode;
import com.ling.lingaicodegeneration.exception.ThrowUtils;
import dev.langchain4j.community.store.memory.chat.redis.RedisChatMemoryStore;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/diagnostics")
public class DiagnosticsController {

    @Resource
    private RedisChatMemoryStore redisChatMemoryStore;

    @Value("${context.pruning.diagnostics.enabled:false}")
    private boolean contextPruningDiagnosticsEnabled;

    @PostMapping("/chat-memory/clear")
    public BaseResponse<?> clearChatMemory(@RequestParam Long appId) {
        if (!contextPruningDiagnosticsEnabled) {
            return ResultUtils.error(
                    ErrorCode.FORBIDDEN_ERROR,
                    "Diagnostics endpoints are disabled"
            );
        }
        ThrowUtils.throwIf(appId == null || appId <= 0, ErrorCode.PARAMS_ERROR);

        redisChatMemoryStore.deleteMessages(appId);
        return ResultUtils.success(Map.of(
                "appId", appId,
                "redisChatMemoryCleared", true
        ));
    }
}
