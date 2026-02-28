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

@Slf4j
public class FileReadTool {

    /**
     * Read the content of a single file
     */
    @Tool("Read the content of a file at the specified relative path")
    public String readFile(
            @P("Relative file path") String relativeFilePath,
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
            String content = Files.readString(path);
            log.info("Successfully read file: {}", path.toAbsolutePath());
            return content;
        } catch (IOException e) {
            String errorMessage = "Read file failed: " + relativeFilePath + ", error: " + e.getMessage();
            log.error(errorMessage, e);
            return errorMessage;
        }
    }
}