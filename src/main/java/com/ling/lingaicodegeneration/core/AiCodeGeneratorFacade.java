package com.ling.lingaicodegeneration.core;

import com.ling.lingaicodegeneration.ai.AiCodeGeneratorService;
import com.ling.lingaicodegeneration.ai.AiCodeGeneratorServiceFactory;
import com.ling.lingaicodegeneration.ai.model.HtmlCodeResult;
import com.ling.lingaicodegeneration.ai.model.MultiFileCodeResult;
import com.ling.lingaicodegeneration.ai.model.message.*;
import com.ling.lingaicodegeneration.constant.AppConstant;
import com.ling.lingaicodegeneration.core.builder.VueProjectBuilder;
import com.ling.lingaicodegeneration.exception.BusinessException;
import com.ling.lingaicodegeneration.exception.ErrorCode;
import com.ling.lingaicodegeneration.model.enums.CodeGenTypeEnum;
import cn.hutool.json.JSONUtil;
import dev.langchain4j.service.TokenStream;
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

    @Resource
    private VueProjectBuilder vueProjectBuilder;

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
        log.info("generateAndSaveCodeStream called, appId: {}, codeGenType: {}", appId, codeGenTypeEnum);
        AiCodeGeneratorService aiService = aiCodeGeneratorServiceFactory
                .getAiCodeGeneratorService(appId, codeGenTypeEnum);
        return switch (codeGenTypeEnum) {
            case HTML -> {
                Flux<String> codeStream = aiService.generateHtmlCodeStream(userMessage);
                yield processCodeStream(codeStream, CodeGenTypeEnum.HTML, appId);
            }
            case MULTI_FILE -> {
                Flux<String> codeStream = aiService.generateMultiFileCodeStream(userMessage);
                yield processCodeStream(codeStream, CodeGenTypeEnum.MULTI_FILE, appId);
            }
            case VUE_PROJECT -> {
                // Vue project uses TokenStream (for tool call events), convert to Flux<String>
                TokenStream tokenStream = aiService.generateVueProjectCodeStream(appId, userMessage);
                yield processTokenStream(tokenStream, appId);
            }
            default -> throw new BusinessException(ErrorCode.SYSTEM_ERROR,
                    "Unsupported generation type: " + codeGenTypeEnum.getValue());
        };
    }

    private File generateAndSaveHtmlCode(String userMessage, Long appId) {
        AiCodeGeneratorService aiService = aiCodeGeneratorServiceFactory
                .getAiCodeGeneratorService(appId, CodeGenTypeEnum.HTML);
        HtmlCodeResult result = aiService.generateHtmlCode(userMessage);
        return CodeFileSaver.saveHtmlCodeResult(result, appId);
    }

    private File generateAndSaveMultiFileCode(String userMessage, Long appId) {
        AiCodeGeneratorService aiService = aiCodeGeneratorServiceFactory
                .getAiCodeGeneratorService(appId, CodeGenTypeEnum.MULTI_FILE);
        MultiFileCodeResult result = aiService.generateMultiFileCode(userMessage);
        return CodeFileSaver.saveMultiFileCodeResult(result, appId);
    }

    private Flux<String> processCodeStream(Flux<String> codeStream, CodeGenTypeEnum codeGenType, Long appId) {
        StringBuilder codeBuilder = new StringBuilder();
        return codeStream
                .doOnNext(codeBuilder::append)
                .doOnComplete(() -> {
                    try {
                        String completeCode = codeBuilder.toString();
                        if (codeGenType == CodeGenTypeEnum.HTML) {
                            HtmlCodeResult htmlCodeResult = CodeParser.parseHtmlCode(completeCode);
                            CodeFileSaver.saveHtmlCodeResult(htmlCodeResult, appId);
                        } else {
                            MultiFileCodeResult multiFileCodeResult = CodeParser.parseMultiFileCode(completeCode);
                            CodeFileSaver.saveMultiFileCodeResult(multiFileCodeResult, appId);
                        }
                    } catch (Exception e) {
                        log.error("Failed to save code: {}", e.getMessage());
                    }
                });
    }

    /**
     * Convert TokenStream to Flux<String>, forwarding tool execution events downstream.
     * Adapter pattern: unifies TokenStream and Flux<String> for the handler layer.
     *
     * Chapter 11 change: Vue project is now built SYNCHRONOUSLY inside onCompleteResponse.
     * The SSE stream only completes AFTER npm install + npm run build finish,
     * so the frontend can immediately preview the result without a blank page.
     */
    private Flux<String> processTokenStream(TokenStream tokenStream, Long appId) {
        return Flux.create(sink -> {
            tokenStream
                    .onPartialResponse(partialResponse -> {
                        AiResponseMessage aiResponseMessage = new AiResponseMessage(partialResponse);
                        sink.next(JSONUtil.toJsonStr(aiResponseMessage));
                    })
                    .onToolExecuted(toolExecution -> {
                        ToolExecutedMessage toolExecutedMessage = new ToolExecutedMessage(toolExecution);
                        sink.next(JSONUtil.toJsonStr(toolExecutedMessage));
                    })
                    .onCompleteResponse(response -> {
                        // Synchronous build: stream only completes after build finishes.
                        // User can immediately preview without manually refreshing.
                        String projectPath = AppConstant.CODE_OUTPUT_ROOT_DIR
                                + File.separator + "vue_project_" + appId;
                        vueProjectBuilder.buildProject(projectPath);
                        sink.complete();
                    })
                    .onError(error -> {
                        sink.error(new RuntimeException(error.getMessage()));
                    })
                    .start();
        });
    }
}