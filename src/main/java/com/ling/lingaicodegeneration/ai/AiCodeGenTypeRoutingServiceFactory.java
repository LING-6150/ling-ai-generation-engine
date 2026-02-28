package com.ling.lingaicodegeneration.ai;

import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Factory for AiCodeGenTypeRoutingService.
 * Uses routingChatModel (no JSON format restriction) since routing returns an enum, not JSON.
 */
@Slf4j
@Configuration
public class AiCodeGenTypeRoutingServiceFactory {

    // Use the dedicated routing model (no response_format: json_object)
    @Resource(name = "routingChatModel")
    private OpenAiChatModel routingChatModel;

    @Bean
    public AiCodeGenTypeRoutingService aiCodeGenTypeRoutingService() {
        return AiServices.builder(AiCodeGenTypeRoutingService.class)
                .chatModel(routingChatModel)
                .build();
    }
}