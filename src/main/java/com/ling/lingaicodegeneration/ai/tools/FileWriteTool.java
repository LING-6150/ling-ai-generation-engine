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
 * 注意：RefineAgent 不应注入此工具（整体覆写风险），仅 FileModifyTool 用于精确替换
 */
@Slf4j
public class FileWriteTool {

    private final String baseDir;

    public FileWriteTool() {
        this.baseDir = null;
    }

    public FileWriteTool(String baseDir) {
        this.baseDir = baseDir;
    }

    /**
     * 写入文件到指定路径
     * AI 通过工具调用的方式写入文件
     */
    @Tool("Write content to a file at the specified path")
    public String writeFile(
            @P("Relative file path") String relativeFilePath,
            @P("Content to write to the file") String content,
            @ToolMemoryId Long appId
    ) {
        try {
            Path path = resolveSafePath(relativeFilePath, appId);
            Path parentDir = path.getParent();
            if (parentDir != null) {
                Files.createDirectories(parentDir);
            }
            Files.write(path, content.getBytes(),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING);
            log.info("Successfully wrote file: {}", path.toAbsolutePath());
            return "File written successfully: " + relativeFilePath;
        } catch (IOException e) {
            String errorMessage = "File write failed: " + relativeFilePath + ", error: " + e.getMessage();
            log.error(errorMessage, e);
            return errorMessage;
        }
    }

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