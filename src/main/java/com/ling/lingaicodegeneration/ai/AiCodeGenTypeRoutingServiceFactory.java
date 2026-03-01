package com.ling.lingaicodegeneration.ai;

import com.ling.lingaicodegeneration.utils.SpringContextUtil;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Factory for AiCodeGenTypeRoutingService.
 * Uses routingChatModelPrototype (no response_format: json_object restriction)
 * since routing returns an enum value, not JSON.
 * Fetches a fresh prototype instance each time to avoid concurrency issues.
 */
@Slf4j
@Configuration
public class AiCodeGenTypeRoutingServiceFactory {

    /**
     * Creates a fresh AiCodeGenTypeRoutingService instance using prototype model bean.
     * Called directly (not via Spring injection) to always get a new model instance.
     */
    @Resource(name = "routingChatModelPrototype")
    private OpenAiChatModel routingChatModel;

    public AiCodeGenTypeRoutingService createAiCodeGenTypeRoutingService() {
        return AiServices.builder(AiCodeGenTypeRoutingService.class)
                .chatModel(routingChatModel)
                .build();
    }

    /**
     * Default Bean — used where @Resource injection is needed (e.g. AppController).
     */
    @Bean
    public AiCodeGenTypeRoutingService aiCodeGenTypeRoutingService() {
        return createAiCodeGenTypeRoutingService();
    }
}