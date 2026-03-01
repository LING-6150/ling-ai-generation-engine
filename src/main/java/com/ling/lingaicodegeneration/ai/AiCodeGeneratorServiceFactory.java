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