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

@Slf4j
public class FileDeleteTool {

    /**
     * Critical files that must never be deleted
     */
    private static final Set<String> IMPORTANT_FILES = Set.of(
            "package.json", "package-lock.json", "yarn.lock",
            "vite.config.js", "vite.config.ts", "vue.config.js",
            "index.html", "main.js", "main.ts", "App.vue",
            "tsconfig.json", "tsconfig.app.json", "tsconfig.node.json",
            ".gitignore", "README.md"
    );

    /**
     * Delete a file at the specified relative path.
     * Important project files are protected and cannot be deleted.
     */
    @Tool("Delete a file at the specified relative path (important project files are protected)")
    public String deleteFile(
            @P("Relative file path to delete") String relativeFilePath,
            @ToolMemoryId Long appId
    ) {
        try {
            Path path = Paths.get(relativeFilePath);
            if (!path.isAbsolute()) {
                String projectDirName = "vue_project_" + appId;
                Path projectRoot = Paths.get(AppConstant.CODE_OUTPUT_ROOT_DIR, projectDirName);
                path = projectRoot.resolve(relativeFilePath);
            }
            if (!Files.exists(path)) {
                return "Warning: file does not exist, no deletion needed - " + relativeFilePath;
            }
            if (!Files.isRegularFile(path)) {
                return "Error: specified path is not a file, cannot delete - " + relativeFilePath;
            }
            // Safety check: protect important files
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

    private boolean isImportantFile(String fileName) {
        return IMPORTANT_FILES.stream()
                .anyMatch(important -> important.equalsIgnoreCase(fileName));
    }
}

