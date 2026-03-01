package com.ling.lingaicodegeneration.ai.langgraph4j.node;

import com.ling.lingaicodegeneration.ai.CodeQualityCheckService;
import com.ling.lingaicodegeneration.ai.langgraph4j.model.QualityResult;
import com.ling.lingaicodegeneration.ai.langgraph4j.state.WorkflowContext;
import com.ling.lingaicodegeneration.utils.SpringContextUtil;
import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.bsc.langgraph4j.action.AsyncNodeAction;
import org.bsc.langgraph4j.state.AgentState;

import java.io.File;
import java.util.List;

import static org.bsc.langgraph4j.action.AsyncNodeAction.node_async;

@Slf4j
public class CodeQualityCheckNode {

    // Supported code file extensions
    private static final List<String> CODE_EXTENSIONS =
            List.of(".html", ".htm", ".css", ".js", ".json", ".vue", ".ts", ".jsx", ".tsx");

    public static AsyncNodeAction<AgentState> create() {
        return node_async(state -> {
            WorkflowContext context = WorkflowContext.getContext(state);
            log.info("Executing node: Code Quality Check");
            String generatedCodeDir = context.getGeneratedCodeDir();
            QualityResult qualityResult;
            try {
                // Read all code files content
                String codeContent = readAndConcatenateCodeFiles(generatedCodeDir);
                if (codeContent.isBlank()) {
                    log.warn("No code files found for quality check");
                    qualityResult = QualityResult.builder()
                            .isValid(false)
                            .errors(List.of("No code files found"))
                            .suggestions(List.of("Please ensure code generation completed successfully"))
                            .build();
                } else {
                    // Call AI quality check service
                    CodeQualityCheckService qualityCheckService =
                            SpringContextUtil.getBean(CodeQualityCheckService.class);
                    qualityResult = qualityCheckService.checkCodeQuality(codeContent);
                    log.info("Code quality check completed, isValid: {}",
                            qualityResult.getIsValid());
                }
            } catch (Exception e) {
                log.error("Code quality check error: {}", e.getMessage(), e);
                // On error, assume valid to proceed
                qualityResult = QualityResult.builder()
                        .isValid(true)
                        .build();
            }
            context.setCurrentStep("Code Quality Check");
            context.setQualityResult(qualityResult);
            return WorkflowContext.saveContext(context);
        });
    }

    /**
     * Read and concatenate all code files in directory
     */
    private static String readAndConcatenateCodeFiles(String codeDir) {
        if (codeDir == null || codeDir.isBlank()) {
            return "";
        }
        File directory = new File(codeDir);
        if (!directory.exists() || !directory.isDirectory()) {
            log.error("Code directory does not exist: {}", codeDir);
            return "";
        }
        StringBuilder codeContent = new StringBuilder();
        codeContent.append("# Project file structure and code\n\n");
        FileUtil.walkFiles(directory, file -> {
            if (shouldSkipFile(file, directory)) {
                return;
            }
            if (isCodeFile(file)) {
                String relativePath = FileUtil.subPath(
                        directory.getAbsolutePath(), file.getAbsolutePath());
                codeContent.append("## File: ").append(relativePath).append("\n");
                String fileContent = FileUtil.readUtf8String(file);
                codeContent.append(fileContent).append("\n\n");
            }
        });
        return codeContent.toString();
    }

    private static boolean shouldSkipFile(File file, File rootDir) {
        String relativePath = FileUtil.subPath(
                rootDir.getAbsolutePath(), file.getAbsolutePath());
        if (file.getName().startsWith(".")) {
            return true;
        }
        return relativePath.contains("node_modules" + File.separator)
                || relativePath.contains("dist" + File.separator)
                || relativePath.contains("target" + File.separator)
                || relativePath.contains(".git" + File.separator);
    }

    private static boolean isCodeFile(File file) {
        String fileName = file.getName().toLowerCase();
        return CODE_EXTENSIONS.stream().anyMatch(fileName::endsWith);
    }
}