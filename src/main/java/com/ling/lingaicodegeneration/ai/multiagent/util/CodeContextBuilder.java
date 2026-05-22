package com.ling.lingaicodegeneration.ai.multiagent.util;

import com.ling.lingaicodegeneration.ai.multiagent.model.RequirementSpec;
import dev.langchain4j.internal.Json;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.Set;
import java.util.stream.Stream;

@Slf4j
public class CodeContextBuilder {

    // mirrors FileDirReadTool.IGNORED_NAMES — keep in sync if either changes
    public static final Set<String> IGNORED_NAMES = Set.of(
            "node_modules", ".git", "dist", "build",
            ".DS_Store", ".env", "target", ".idea", ".vscode"
    );

    public static final int MAX_FILE_CHARS = 5000;

    private CodeContextBuilder() {}

    public static String buildReviewContext(String generatedCodeDir, RequirementSpec spec) {
        StringBuilder sb = new StringBuilder();

        sb.append("## Requirement Specification\n");
        sb.append(Json.toJson(spec));
        sb.append("\n\n## Generated Code Files\n");

        Path root = Paths.get(generatedCodeDir);
        if (!Files.isDirectory(root)) {
            log.warn("CodeContextBuilder: directory not found: {}", generatedCodeDir);
            sb.append("(directory not found: ").append(generatedCodeDir).append(")");
            return sb.toString();
        }

        try (Stream<Path> stream = Files.walk(root)) {
            stream.filter(Files::isRegularFile)
                    .filter(p -> isNotIgnored(root, p))
                    .sorted(Comparator.comparing(p -> root.relativize(p).toString()))
                    .forEach(file -> appendFile(sb, root, file));
        } catch (IOException e) {
            log.error("CodeContextBuilder: error walking {}: {}", generatedCodeDir, e.getMessage());
            sb.append("(error reading files: ").append(e.getMessage()).append(")");
        }

        return sb.toString();
    }

    private static boolean isNotIgnored(Path root, Path file) {
        for (Path part : root.relativize(file)) {
            if (IGNORED_NAMES.contains(part.toString())) return false;
        }
        return true;
    }

    private static void appendFile(StringBuilder sb, Path root, Path file) {
        String relative = root.relativize(file).toString();
        sb.append("\n=== ").append(relative).append(" ===\n");
        try {
            String content = Files.readString(file, StandardCharsets.UTF_8);
            if (content.length() > MAX_FILE_CHARS) {
                sb.append(content, 0, MAX_FILE_CHARS);
                sb.append("\n... (truncated at ").append(MAX_FILE_CHARS).append(" chars)");
            } else {
                sb.append(content);
            }
        } catch (IOException e) {
            sb.append("(error reading file: ").append(e.getMessage()).append(")");
        }
    }
}
