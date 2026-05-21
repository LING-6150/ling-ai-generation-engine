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
public class FileReadTool {

    private final String baseDir;

    public FileReadTool() {
        this.baseDir = null;
    }

    public FileReadTool(String baseDir) {
        this.baseDir = baseDir;
    }

    @Tool("Read the content of a file at the specified relative path")
    public String readFile(
            @P("Relative file path") String relativeFilePath,
            @ToolMemoryId Long appId
    ) {
        try {
            Path path = resolveSafePath(relativeFilePath, appId);
            if (!Files.exists(path) || !Files.isRegularFile(path)) {
                return "Error: file does not exist - " + relativeFilePath;
            }
            String content = Files.readString(path);
            log.info("Successfully read file: {}", path.toAbsolutePath());
            return content;
        } catch (IOException e) {
            String errorMessage = "Read file failed: " + relativeFilePath + ", error: " + e.getMessage();
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