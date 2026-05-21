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
import java.util.Set;

/**
 * 文件工具支持两种构造模式：
 * - 无参构造：兼容 agent=false 路径（VUE_PROJECT 工程生成），
 *   baseDir 从 @ToolMemoryId appId 推导为 "vue_project_{appId}"
 * - baseDir 构造：服务 agent=true 的 RefineAgent 路径，
 *   支持 HTML/MULTI_FILE/VUE_PROJECT 三种代码生成类型
 *
 * 当 baseDir != null 时，appId 参数被忽略（LangChain4j 框架要求保留参数签名）
 * 注意：RefineAgent 不应注入此工具（删除操作在修复阶段风险过高）
 */
@Slf4j
public class FileDeleteTool {

    private final String baseDir;

    public FileDeleteTool() {
        this.baseDir = null;
    }

    public FileDeleteTool(String baseDir) {
        this.baseDir = baseDir;
    }

    /**
     * 受保护的关键文件列表，AI 不允许删除这些文件
     * 包含 Vue 项目的核心配置文件和入口文件
     */
    private static final Set<String> IMPORTANT_FILES = Set.of(
            "package.json", "package-lock.json", "yarn.lock",
            "vite.config.js", "vite.config.ts", "vue.config.js",
            "index.html", "main.js", "main.ts", "App.vue",
            "tsconfig.json", "tsconfig.app.json", "tsconfig.node.json",
            ".gitignore", "README.md"
    );

    /**
     * 删除指定相对路径的文件
     * AI 必须传入相对路径，工具内部拼上项目目录
     * 关键配置文件受保护，不允许删除
     */
    @Tool("Delete a file at the specified relative path (important project files are protected)")
    public String deleteFile(
            @P("Relative file path to delete") String relativeFilePath,
            @ToolMemoryId Long appId
    ) {
        try {
            // 安全路径解析：拒绝绝对路径，防止路径遍历攻击
            Path path = resolveSafePath(relativeFilePath, appId);

            if (!Files.exists(path)) {
                return "Warning: file does not exist, no deletion needed - " + relativeFilePath;
            }
            if (!Files.isRegularFile(path)) {
                return "Error: specified path is not a file, cannot delete - " + relativeFilePath;
            }

            // 黑名单校验：保护关键文件不被 AI 误删
            String fileName = path.getFileName().toString();
            if (isImportantFile(fileName)) {
                return "Error: not allowed to delete important file - " + fileName;
            }

            Files.delete(path);
            log.info("Successfully deleted file: {}", path.toAbsolutePath());
            return "File deleted successfully: " + relativeFilePath;
        } catch (IOException e) {
            String errorMessage = "Delete file failed: " + relativeFilePath + ", error: " + e.getMessage();
            log.error(errorMessage, e);
            return errorMessage;
        }
    }

    /**
     * 判断是否为受保护的关键文件（忽略大小写）
     */
    private boolean isImportantFile(String fileName) {
        return IMPORTANT_FILES.stream()
                .anyMatch(important -> important.equalsIgnoreCase(fileName));
    }

    /**
     * 安全路径解析：
     * 1. 强制拒绝绝对路径，防止 AI 访问项目目录以外的文件
     * 2. normalize() 消除 ../ 等相对路径符号
     * 3. startsWith() 严格校验解析后路径必须在项目目录内
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
        // 校验解析后路径必须在项目目录内，防止 ../../../etc/passwd 类型攻击
        if (!resolvedPath.startsWith(realRoot)) {
            throw new IOException("Path traversal attempt detected: " + relativeFilePath);
        }
        return resolvedPath;
    }
}