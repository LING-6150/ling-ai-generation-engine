package com.ling.lingaicodegeneration.ai.tools;

import com.ling.lingaicodegeneration.constant.AppConstant;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.agent.tool.ToolMemoryId;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * 文件工具支持两种构造模式：
 * - 无参构造：兼容 agent=false 路径（VUE_PROJECT 工程生成），
 *   baseDir 从 @ToolMemoryId appId 推导为 "vue_project_{appId}"
 * - baseDir 构造：服务 agent=true 的 RefineAgent 路径，
 *   支持 HTML/MULTI_FILE/VUE_PROJECT 三种代码生成类型
 *
 * 当 baseDir != null 时，appId 参数被忽略（LangChain4j 框架要求保留参数签名）
 */
@Slf4j
public class FileModifyTool {

    private final String baseDir;

    public FileModifyTool() {
        this.baseDir = null;
    }

    public FileModifyTool(String baseDir) {
        this.baseDir = baseDir;
    }

    /**
     * Precisely modify file content by replacing old content with new content.
     * AI must first read the file, then provide the exact old content to replace.
     */
    @Tool("Modify file content: replace the specified old content with new content in the given file")
    public String modifyFile(
            @P("Relative file path") String relativeFilePath,
            @P("The exact old content to be replaced (must match exactly)") String oldContent,
            @P("The new content to replace with") String newContent,
            @ToolMemoryId Long appId
    ) {
        try {
            Path path = resolveSafePath(relativeFilePath, appId);

            if (!Files.exists(path) || !Files.isRegularFile(path)) {
                return "Error: file does not exist - " + relativeFilePath;
            }

            String originalContent = Files.readString(path);

            if (!originalContent.contains(oldContent)) {
                return "Warning: old content not found in file, no modification made - " + relativeFilePath
                        + ". Please use [Read File Tool] to get the exact current content first.";
            }

            String modifiedContent = originalContent.replace(oldContent, newContent);

            if (originalContent.equals(modifiedContent)) {
                return "Info: content unchanged after replacement - " + relativeFilePath;
            }

            Files.writeString(path, modifiedContent,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING);

            log.info("Successfully modified file: {}", path.toAbsolutePath());
            return String.format(
                    "File modified successfully: %s\nReplaced:\n```\n%s\n```\nWith:\n```\n%s\n```",
                    relativeFilePath, oldContent, newContent
            );
        } catch (IOException e) {
            String errorMessage = "Modify file failed: " + relativeFilePath + ", error: " + e.getMessage();
            log.error(errorMessage, e);
            return errorMessage;
        }
    }

    /**
     * 安全路径解析：
     * 1. 强制拒绝绝对路径
     * 2. normalize() 消除 ../ 等相对路径符号
     * 3. startsWith() 严格校验必须在项目目录内
     */
    private Path resolveSafePath(String relativeFilePath, Long appId) throws IOException {
        if (Paths.get(relativeFilePath).isAbsolute()) {
            throw new IOException("Absolute paths are not allowed: " + relativeFilePath);
        }
        Path projectRoot = (baseDir != null)
                ? Paths.get(baseDir)
                : Paths.get(AppConstant.CODE_OUTPUT_ROOT_DIR, "vue_project_" + appId);
        Files.createDirectories(projectRoot);
        Path realRoot = projectRoot.toRealPath();
        Path resolvedPath = realRoot.resolve(relativeFilePath).normalize();
        if (!resolvedPath.startsWith(realRoot)) {
            throw new IOException("Path traversal attempt detected: " + relativeFilePath);
        }
        return resolvedPath;
    }
}