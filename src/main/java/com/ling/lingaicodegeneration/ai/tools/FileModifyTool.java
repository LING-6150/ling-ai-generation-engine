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

@Slf4j
public class FileModifyTool {

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
            Path path = Paths.get(relativeFilePath);
            if (!path.isAbsolute()) {
                String projectDirName = "vue_project_" + appId;
                Path projectRoot = Paths.get(AppConstant.CODE_OUTPUT_ROOT_DIR, projectDirName);
                path = projectRoot.resolve(relativeFilePath);
            }
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
}