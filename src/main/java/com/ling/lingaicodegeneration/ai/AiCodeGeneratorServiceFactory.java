package com.ling.lingaicodegeneration.ai;

import com.ling.lingaicodegeneration.ai.guardrail.PromptSafetyInputGuardrail;
import com.ling.lingaicodegeneration.ai.guardrail.RetryOutputGuardrail;
import com.ling.lingaicodegeneration.ai.tools.*;
import com.ling.lingaicodegeneration.exception.BusinessException;
import com.ling.lingaicodegeneration.exception.ErrorCode;
import com.ling.lingaicodegeneration.model.enums.CodeGenTypeEnum;
import com.ling.lingaicodegeneration.service.ChatHistoryService;
import com.ling.lingaicodegeneration.utils.SpringContextUtil;
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

@Slf4j
@Configuration
public class AiCodeGeneratorServiceFactory {

    @Resource(name = "openAiChatModel")
    private ChatModel chatModel;

    @Resource
    private RedisChatMemoryStore redisChatMemoryStore;

    @Resource
    //@Lazy 是干嘛的？
    //解决循环依赖。ChatHistoryService 里用到了 AppService，AppService 又用到了这个 Factory，Spring 启动时两边互相等对方，会报循环依赖错误。
    // 加 @Lazy 延迟加载，第一次真正调用时才注入，绕开这个问题。
    @Lazy
    private ChatHistoryService chatHistoryService;

    /**
     * Get AI service for the given appId and codeGenType.
     *
     * Key change from Chapter 7/8/9:
     * - Previously: Caffeine cache reused same instance → concurrent requests on same ChatModel
     *   triggered blocking in SpringRestClient.execute() → requests queued up
     * - Now: Each call gets a FRESH instance via SpringContextUtil.getBean("prototype bean")
     *   → every request has its own StreamingChatModel → no contention
     *  旧方案：Caffeine 缓存 → 复用同一实例
     * 根本原因：SpringRestClient.execute() 同步阻塞
     * 新方案：每次 getBean() 拿新实例 → 各自独立
     *
     * Chat memory is still persisted in Redis (data not lost), only the in-memory
     * MessageWindowChatMemory wrapper is recreated each time.
     */
    public AiCodeGeneratorService getAiCodeGeneratorService(long appId, CodeGenTypeEnum codeGenType) {
        log.info("Creating new AI service instance for appId: {}, codeGenType: {}", appId, codeGenType);
        return createAiCodeGeneratorService(appId, codeGenType);
    }

    /**
     * Backward-compatible overload, defaults to HTML mode.
     */
    public AiCodeGeneratorService getAiCodeGeneratorService(long appId) {
        return getAiCodeGeneratorService(appId, CodeGenTypeEnum.HTML);
    }

    /**
     *  Memory 对象每次新建，但数据从 Redis 加载，历史不丢失
     * Build a fresh MessageWindowChatMemory backed by Redis.
     * The object is new each time, but data is loaded from Redis — no history lost.
     */
    private MessageWindowChatMemory buildChatMemory(long appId) {
        MessageWindowChatMemory chatMemory = MessageWindowChatMemory.builder()
                .id(appId)
                .chatMemoryStore(redisChatMemoryStore)
                .maxMessages(20)
                .build();
        // Load persisted chat history from DB into memory
        chatHistoryService.loadChatHistoryToMemory(appId, chatMemory, 20);
        return chatMemory;
    }

    /**
     * Create a new AI service instance.
     * Uses SpringContextUtil.getBean() to fetch @Scope("prototype") model beans,
     * ensuring each service instance gets its own dedicated model connection.
     */
    private AiCodeGeneratorService createAiCodeGeneratorService(long appId, CodeGenTypeEnum codeGenType) {
        MessageWindowChatMemory chatMemory = buildChatMemory(appId);

        return switch (codeGenType) {
            // Vue project: reasoning streaming model + tool calling
            case VUE_PROJECT -> {
                // Fetch a fresh prototype instance of the reasoning streaming model
                OpenAiStreamingChatModel reasoningStreamingChatModel =
                        //这里用 SpringContextUtil.getBean() 而不是字段注入，这就是并发问题的解法。
                        // 每次调用这个方法都触发一次 prototype Bean 的新建
                        SpringContextUtil.getBean("reasoningStreamingChatModel",
                                OpenAiStreamingChatModel.class);
                yield AiServices.builder(AiCodeGeneratorService.class)
                        .streamingChatModel(reasoningStreamingChatModel)
                        .chatMemoryProvider(memoryId -> chatMemory)
                        .tools(
                                new FileWriteTool(),
                                new FileReadTool(),
                                new FileDirReadTool(),
                                new FileModifyTool(),
                                new FileDeleteTool()
                        )
                        //AI 有时候会幻觉出一个不存在的工具名，正常情况下会抛异常导致整个生成失败。这里配置了降级策略，
                        // 返回一条错误消息给 AI，让它自己纠正，不中断生成流程。
                        // Handle hallucinated tool names gracefully
                        .hallucinatedToolNameStrategy(toolExecutionRequest ->
                                ToolExecutionResultMessage.from(toolExecutionRequest,
                                        "Error: there is no tool called " + toolExecutionRequest.name()))
                        // Prevent infinite tool call loops
                        .maxSequentialToolsInvocations(20)
                        .inputGuardrails(new PromptSafetyInputGuardrail())  // 加这里
                        .build();

            }
            // HTML / MULTI_FILE: standard streaming model
            case HTML, MULTI_FILE -> {
                // Fetch a fresh prototype instance of the standard streaming model
                StreamingChatModel streamingChatModel =
                        SpringContextUtil.getBean("openAiStreamingChatModel",
                                StreamingChatModel.class);
                yield AiServices.builder(AiCodeGeneratorService.class)
                        .chatModel(chatModel)
                        .streamingChatModel(streamingChatModel)
                        .chatMemory(chatMemory)
                        .inputGuardrails(new PromptSafetyInputGuardrail())   // 加这里
                        .outputGuardrails(new RetryOutputGuardrail())         // 加这里
                        .build();
            }
            default -> throw new BusinessException(ErrorCode.SYSTEM_ERROR,
                    "Unsupported code gen type: " + codeGenType.getValue());
        };
    }

    /**
     * Default Bean for backward compatibility.
     */
    @Bean
    public AiCodeGeneratorService aiCodeGeneratorService() {
        return getAiCodeGeneratorService(0L, CodeGenTypeEnum.HTML);
    }
}