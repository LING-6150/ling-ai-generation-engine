package com.ling.lingaicodegeneration.ai.tools;

import cn.hutool.core.io.FileUtil;
import com.ling.lingaicodegeneration.constant.AppConstant;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.agent.tool.ToolMemoryId;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

@Slf4j
public class FileDirReadTool {

    /**
     * Directories and files to ignore when reading structure
     */
    private static final Set<String> IGNORED_NAMES = Set.of(
            "node_modules", ".git", "dist", "build",
            ".DS_Store", ".env", "target", ".idea", ".vscode"
    );

    /**
     * Read the directory structure of the project
     */
    @Tool("Read the directory structure, listing all files and subdirectories under the given relative path")
    public String readDir(
            @P("Relative directory path, pass empty string to read the project root") String relativeDirPath,
            @ToolMemoryId Long appId
    ) {
        try {
            String projectDirName = "vue_project_" + appId;
            Path projectRoot = Paths.get(AppConstant.CODE_OUTPUT_ROOT_DIR, projectDirName);
            Path targetPath = relativeDirPath == null || relativeDirPath.isBlank()
                    ? projectRoot
                    : projectRoot.resolve(relativeDirPath);

            File targetDir = targetPath.toFile();
            if (!targetDir.exists() || !targetDir.isDirectory()) {
                return "Error: directory does not exist - " + relativeDirPath;
            }

            StringBuilder structure = new StringBuilder();
            structure.append("Project directory structure:\n");

            // Use Hutool to list all files recursively
            List<File> allFiles = FileUtil.loopFiles(targetDir,
                    file -> !IGNORED_NAMES.contains(file.getName()));

            allFiles.stream()
                    .sorted((f1, f2) -> {
                        // Directories first, then by depth, then by name
                        int depth1 = getRelativeDepth(targetDir, f1);
                        int depth2 = getRelativeDepth(targetDir, f2);
                        if (depth1 != depth2) return Integer.compare(depth1, depth2);
                        return f1.getPath().compareTo(f2.getPath());
                    })
                    .forEach(file -> {
                        int depth = getRelativeDepth(targetDir, file);
                        String indent = "  ".repeat(depth);
                        // Show relative path from project root
                        String relativePath = projectRoot.relativize(file.toPath()).toString();
                        structure.append(indent).append(relativePath).append("\n");
                    });

            log.info("Successfully read directory: {}", targetDir.getAbsolutePath());
            return structure.toString();
        } catch (Exception e) {
            String errorMessage = "Read directory failed: " + relativeDirPath + ", error: " + e.getMessage();
            log.error(errorMessage, e);
            return errorMessage;
        }
    }

    private int getRelativeDepth(File root, File file) {
        Path rootPath = root.toPath();
        Path filePath = file.toPath();
        return rootPath.relativize(filePath).getNameCount() - 1;
    }
}