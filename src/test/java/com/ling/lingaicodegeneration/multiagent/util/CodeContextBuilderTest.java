package com.ling.lingaicodegeneration.multiagent.util;

import com.ling.lingaicodegeneration.ai.multiagent.model.Complexity;
import com.ling.lingaicodegeneration.ai.multiagent.model.RequirementSpec;
import com.ling.lingaicodegeneration.ai.multiagent.util.CodeContextBuilder;
import com.ling.lingaicodegeneration.model.enums.CodeGenTypeEnum;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CodeContextBuilderTest {

    @TempDir
    Path tempDir;

    private RequirementSpec spec() {
        return RequirementSpec.builder()
                .codeGenType(CodeGenTypeEnum.HTML)
                .projectDescription("test project")
                .features(List.of("display heading"))
                .techConstraints(List.of("plain HTML"))
                .complexity(Complexity.SIMPLE)
                .build();
    }

    @Test
    void includesHtmlAndCssFiles() throws IOException {
        Files.writeString(tempDir.resolve("index.html"), "<html><body>Hello</body></html>");
        Files.writeString(tempDir.resolve("style.css"), "body { margin: 0; }");

        String context = CodeContextBuilder.buildReviewContext(tempDir.toString(), spec());

        assertTrue(context.contains("=== index.html ==="));
        assertTrue(context.contains("=== style.css ==="));
        assertTrue(context.contains("<html>"));
        assertTrue(context.contains("body { margin: 0; }"));
    }

    @Test
    void ignoresNodeModules() throws IOException {
        Files.writeString(tempDir.resolve("index.html"), "<html/>");
        Path nm = Files.createDirectories(tempDir.resolve("node_modules"));
        Files.writeString(nm.resolve("foo.js"), "should-be-ignored");

        String context = CodeContextBuilder.buildReviewContext(tempDir.toString(), spec());

        assertTrue(context.contains("index.html"));
        assertFalse(context.contains("foo.js"), "node_modules must be filtered");
        assertFalse(context.contains("should-be-ignored"));
    }

    @Test
    void ignoresDist() throws IOException {
        Files.writeString(tempDir.resolve("index.html"), "<html/>");
        Path dist = Files.createDirectories(tempDir.resolve("dist"));
        Files.writeString(dist.resolve("bundle.js"), "bundled-content");

        String context = CodeContextBuilder.buildReviewContext(tempDir.toString(), spec());

        assertFalse(context.contains("bundle.js"), "dist must be filtered");
        assertFalse(context.contains("bundled-content"));
    }

    @Test
    void truncatesLargeFiles() throws IOException {
        String bigContent = "x".repeat(CodeContextBuilder.MAX_FILE_CHARS + 2000);
        Files.writeString(tempDir.resolve("big.html"), bigContent);

        String context = CodeContextBuilder.buildReviewContext(tempDir.toString(), spec());

        assertTrue(context.contains("truncated at " + CodeContextBuilder.MAX_FILE_CHARS + " chars"),
                "Large files must be truncated with a marker");
        assertFalse(context.contains("x".repeat(CodeContextBuilder.MAX_FILE_CHARS + 1)),
                "Content past MAX_FILE_CHARS must not appear");
    }

    @Test
    void smallFileIsNotTruncated() throws IOException {
        String content = "a".repeat(100);
        Files.writeString(tempDir.resolve("small.html"), content);

        String context = CodeContextBuilder.buildReviewContext(tempDir.toString(), spec());

        assertTrue(context.contains(content));
        assertFalse(context.contains("truncated"));
    }

    @Test
    void nonExistentDirectory_returnsWarningMessage() {
        String context = CodeContextBuilder.buildReviewContext(
                "/tmp/nonexistent-review-test-dir-xyz", spec());

        assertTrue(context.contains("directory not found"));
    }

    @Test
    void includesRequirementSpecJson() throws IOException {
        Files.writeString(tempDir.resolve("index.html"), "<html/>");

        String context = CodeContextBuilder.buildReviewContext(tempDir.toString(), spec());

        assertTrue(context.contains("## Requirement Specification"));
        assertTrue(context.contains("projectDescription"));
        assertTrue(context.contains("test project"));
    }
}
