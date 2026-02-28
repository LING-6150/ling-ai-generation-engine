package com.ling.lingaicodegeneration.ai;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.ling.lingaicodegeneration.ai.tools.FileDeleteTool;
import com.ling.lingaicodegeneration.ai.tools.FileDirReadTool;
import com.ling.lingaicodegeneration.ai.tools.FileModifyTool;
import com.ling.lingaicodegeneration.ai.tools.FileReadTool;
import com.ling.lingaicodegeneration.ai.tools.FileWriteTool;
import com.ling.lingaicodegeneration.model.enums.CodeGenTypeEnum;
import com.ling.lingaicodegeneration.service.ChatHistoryService;
import dev.langchain4j.community.store.memory.chat.redis.RedisChatMemoryStore;
import dev.langchain4j.data.message.ToolExecutionResultMessage;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.time.Duration;

@Slf4j
@Configuration
public class AiCodeGeneratorServiceFactory {

    @Resource(name = "openAiChatModel")
    private ChatModel chatModel;

    @Resource(name = "openAiStreamingChatModel")
    private StreamingChatModel streamingChatModel;

    @Resource(name = "reasoningStreamingChatModel")
    private OpenAiStreamingChatModel reasoningStreamingChatModel;


    @Resource
    private RedisChatMemoryStore redisChatMemoryStore;

    @Resource
    @Lazy
    private ChatHistoryService chatHistoryService;

    /**
     * AI 服务实例缓存
     * - 最大缓存 1000 个实例
     * - 写入后 30 分钟过期
     * - 访问后 10 分钟过期
     */
    private final Cache<String, AiCodeGeneratorService> serviceCache = Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(Duration.ofMinutes(30))
            .expireAfterAccess(Duration.ofMinutes(10))
            .removalListener((key, value, cause) -> {
                log.debug("AI service instance removed, key: {}, cause: {}", key, cause);
            })
            .build();

    /**
     * 根据 appId 获取服务（带缓存）
     */
    public AiCodeGeneratorService getAiCodeGeneratorService(long appId, CodeGenTypeEnum codeGenType) {
        // 缓存 key 包含 codeGenType，因为不同生成类型使用不同模型
        String cacheKey = appId + "_" + codeGenType.getValue();
        return serviceCache.get(cacheKey, key -> createAiCodeGeneratorService(appId, codeGenType));
    }

    /**
     * 向后兼容，默认使用 HTML 模式
     */
    public AiCodeGeneratorService getAiCodeGeneratorService(long appId) {
        return getAiCodeGeneratorService(appId, CodeGenTypeEnum.HTML);
    }

    /**
     * 创建新的 AI 服务实例
     */
    private AiCodeGeneratorService createAiCodeGeneratorService(long appId, CodeGenTypeEnum codeGenType) {
        log.info("Creating new AI service instance for appId: {}, codeGenType: {}", appId, codeGenType);
        // 根据 appId 构建独立的对话记忆
        MessageWindowChatMemory chatMemory = MessageWindowChatMemory
                .builder()
                .id(appId)
                .chatMemoryStore(redisChatMemoryStore)
                .maxMessages(20)
                .build();
        // 从数据库加载历史对话到记忆中
        chatHistoryService.loadChatHistoryToMemory(appId, chatMemory, 20);

        // 根据代码生成类型选择不同的模型配置
        return switch (codeGenType) {
            // Vue 项目使用推理流式模型 + 工具调用
            case VUE_PROJECT -> AiServices.builder(AiCodeGeneratorService.class)
                    .streamingChatModel(reasoningStreamingChatModel)
                    .chatMemoryProvider(memoryId -> chatMemory)
                    .tools(
                            new FileWriteTool(),
                            new FileReadTool(),
                            new FileDirReadTool(),
                            new FileModifyTool(),
                            new FileDeleteTool()
                    )
                    .hallucinatedToolNameStrategy(toolExecutionRequest -> ToolExecutionResultMessage.from(
                            toolExecutionRequest, "Error: there is no tool called " + toolExecutionRequest.name()))
                    .build();
            // HTML 和多文件使用默认模型
            default -> AiServices.builder(AiCodeGeneratorService.class)
                    .chatModel(chatModel)
                    .streamingChatModel(streamingChatModel)
                    .chatMemory(chatMemory)
                    .build();
        };
    }

    /**
     * 默认提供一个 Bean，保持向后兼容
     */
    @Bean
    public AiCodeGeneratorService aiCodeGeneratorService() {
        return getAiCodeGeneratorService(0L, CodeGenTypeEnum.HTML);
    }
}