package com.ling.lingaicodegeneration.core;

import com.ling.lingaicodegeneration.ai.AiCodeGeneratorService;
import com.ling.lingaicodegeneration.ai.AiCodeGeneratorServiceFactory;
import com.ling.lingaicodegeneration.ai.model.HtmlCodeResult;
import com.ling.lingaicodegeneration.ai.model.MultiFileCodeResult;
import com.ling.lingaicodegeneration.exception.BusinessException;
import com.ling.lingaicodegeneration.exception.ErrorCode;
import com.ling.lingaicodegeneration.model.enums.CodeGenTypeEnum;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.io.File;

@Slf4j
@Service
public class AiCodeGeneratorFacade {

    @Resource
    private AiCodeGeneratorServiceFactory aiCodeGeneratorServiceFactory;

    /**
     * Generate and save code (non-streaming)
     */
    public File generateAndSaveCode(String userMessage, CodeGenTypeEnum codeGenTypeEnum, Long appId) {
        if (codeGenTypeEnum == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Code generation type cannot be null");
        }
        return switch (codeGenTypeEnum) {
            case HTML -> generateAndSaveHtmlCode(userMessage, appId);
            case MULTI_FILE -> generateAndSaveMultiFileCode(userMessage, appId);
            default -> throw new BusinessException(ErrorCode.SYSTEM_ERROR,
                    "Unsupported generation type: " + codeGenTypeEnum.getValue());
        };
    }

    /**
     * Generate and save code (streaming)
     */
    public Flux<String> generateAndSaveCodeStream(String userMessage, CodeGenTypeEnum codeGenTypeEnum, Long appId) {
        if (codeGenTypeEnum == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Code generation type cannot be null");
        }
        return switch (codeGenTypeEnum) {
            case HTML -> generateAndSaveHtmlCodeStream(userMessage, appId);
            case MULTI_FILE -> generateAndSaveMultiFileCodeStream(userMessage, appId);
            default -> throw new BusinessException(ErrorCode.SYSTEM_ERROR,
                    "Unsupported generation type: " + codeGenTypeEnum.getValue());
        };
    }

    private File generateAndSaveHtmlCode(String userMessage, Long appId) {
        // 根据 appId 获取对应的 AI Service 实例
        AiCodeGeneratorService aiService = aiCodeGeneratorServiceFactory.getAiCodeGeneratorService(appId);
        HtmlCodeResult result = aiService.generateHtmlCode(userMessage);
        return CodeFileSaver.saveHtmlCodeResult(result, appId);
    }

    private File generateAndSaveMultiFileCode(String userMessage, Long appId) {
        AiCodeGeneratorService aiService = aiCodeGeneratorServiceFactory.getAiCodeGeneratorService(appId);
        MultiFileCodeResult result = aiService.generateMultiFileCode(userMessage);
        return CodeFileSaver.saveMultiFileCodeResult(result, appId);
    }

    private Flux<String> generateAndSaveHtmlCodeStream(String userMessage, Long appId) {
        AiCodeGeneratorService aiService = aiCodeGeneratorServiceFactory.getAiCodeGeneratorService(appId);
        Flux<String> codeStream = aiService.generateHtmlCodeStream(userMessage);
        StringBuilder codeBuilder = new StringBuilder();
        return codeStream
                .doOnNext(codeBuilder::append)
                .doOnComplete(() -> {
                    try {
                        String completeCode = codeBuilder.toString();
                        HtmlCodeResult htmlCodeResult = CodeParser.parseHtmlCode(completeCode);
                        CodeFileSaver.saveHtmlCodeResult(htmlCodeResult, appId);
                    } catch (Exception e) {
                        log.error("Failed to save HTML code: {}", e.getMessage());
                    }
                });
    }

    private Flux<String> generateAndSaveMultiFileCodeStream(String userMessage, Long appId) {
        AiCodeGeneratorService aiService = aiCodeGeneratorServiceFactory.getAiCodeGeneratorService(appId);
        Flux<String> codeStream = aiService.generateMultiFileCodeStream(userMessage);
        StringBuilder codeBuilder = new StringBuilder();
        return codeStream
                .doOnNext(codeBuilder::append)
                .doOnComplete(() -> {
                    try {
                        String completeCode = codeBuilder.toString();
                        MultiFileCodeResult multiFileCodeResult = CodeParser.parseMultiFileCode(completeCode);
                        CodeFileSaver.saveMultiFileCodeResult(multiFileCodeResult, appId);
                    } catch (Exception e) {
                        log.error("Failed to save multi-file code: {}", e.getMessage());
                    }
                });
    }
}