package com.ling.lingaicodegeneration.ai.tools;

import cn.hutool.core.io.FileUtil;
import com.ling.lingaicodegeneration.constant.AppConstant;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.agent.tool.ToolMemoryId;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

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
public class FileDirReadTool {

    private final String baseDir;

    public FileDirReadTool() {
        this.baseDir = null;
    }

    public FileDirReadTool(String baseDir) {
        this.baseDir = baseDir;
    }

    /**
     * 遍历目录时忽略的文件夹和文件名
     * 避免把 node_modules 等大型无关目录返回给 AI，浪费 token
     */
    private static final Set<String> IGNORED_NAMES = Set.of(
            "node_modules", ".git", "dist", "build",
            ".DS_Store", ".env", "target", ".idea", ".vscode"
    );

    /**
     * 读取项目目录结构
     * AI 调用此工具了解项目有哪些文件，再决定读取或修改哪个文件
     * relativeDirPath 为空时读取项目根目录
     */
    @Tool("Read the directory structure, listing all files and subdirectories under the given relative path")
    public String readDir(
            @P("Relative directory path, pass empty string to read the project root") String relativeDirPath,
            @ToolMemoryId Long appId
    ) {
        try {
            Path projectRoot = (baseDir != null)
                    ? Paths.get(baseDir)
                    : Paths.get(AppConstant.CODE_OUTPUT_ROOT_DIR, "vue_project_" + appId);
            Files.createDirectories(projectRoot);
            Path realRoot = projectRoot.toRealPath();

            // 路径安全校验：relativeDirPath 为空时读根目录，否则校验路径合法性
            Path targetPath;
            if (relativeDirPath == null || relativeDirPath.isBlank()) {
                targetPath = realRoot;
            } else {
                // 拒绝绝对路径
                if (Paths.get(relativeDirPath).isAbsolute()) {
                    return "Error: Absolute paths are not allowed: " + relativeDirPath;
                }
                targetPath = realRoot.resolve(relativeDirPath).normalize();
                // 校验必须在项目目录内，防止路径遍历攻击
                if (!targetPath.startsWith(realRoot)) {
                    return "Error: Path traversal attempt detected: " + relativeDirPath;
                }
            }

            File targetDir = targetPath.toFile();
            if (!targetDir.exists() || !targetDir.isDirectory()) {
                return "Error: directory does not exist - " + relativeDirPath;
            }

            StringBuilder structure = new StringBuilder();
            structure.append("Project directory structure:\n");

            // 递归遍历所有文件，过滤掉忽略列表中的文件和目录
            List<File> allFiles = FileUtil.loopFiles(targetDir,
                    file -> !IGNORED_NAMES.contains(file.getName()));

            // 按深度排序，同深度按路径字母排序，让目录结构更直观
            allFiles.stream()
                    .sorted((f1, f2) -> {
                        int depth1 = getRelativeDepth(targetDir, f1);
                        int depth2 = getRelativeDepth(targetDir, f2);
                        if (depth1 != depth2) return Integer.compare(depth1, depth2);
                        return f1.getPath().compareTo(f2.getPath());
                    })
                    .forEach(file -> {
                        int depth = getRelativeDepth(targetDir, file);
                        String indent = "  ".repeat(depth);
                        // 返回相对于项目根目录的路径，不暴露服务器绝对路径
                        String relativePath = realRoot.relativize(file.toPath()).toString();
                        structure.append(indent).append(relativePath).append("\n");
                    });

            log.info("Successfully read directory: {}", targetDir.getAbsolutePath());
            return structure.toString();
        } catch (IOException e) {
            String errorMessage = "Read directory failed: " + relativeDirPath + ", error: " + e.getMessage();
            log.error(errorMessage, e);
            return errorMessage;
        }
    }

    /**
     * 计算文件相对于根目录的深度
     * 用于目录结构的缩进显示
     */
    private int getRelativeDepth(File root, File file) {
        Path rootPath = root.toPath();
        Path filePath = file.toPath();
        return rootPath.relativize(filePath).getNameCount() - 1;
    }
}