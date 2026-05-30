package com.ling.lingaicodegeneration.core.builder;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class VueProjectBuilderTest {

    private final VueProjectBuilder builder = new VueProjectBuilder();

    @TempDir
    Path tempDir;

    @Test
    void buildProjectWithResult_missingProjectDirectory_returnsStableFailure() {
        VueBuildResult result = builder.buildProjectWithResult(tempDir.resolve("missing").toString());

        assertFalse(result.passed());
        assertTrue(result.durationMillis() >= 0);
        assertEquals("", result.outputSnippet());
        assertTrue(result.errorSnippet().contains("项目目录不存在"));
    }

    @Test
    void buildProjectWithResult_missingPackageJson_returnsStableFailure() throws Exception {
        Path projectDir = Files.createDirectory(tempDir.resolve("vue-project"));

        VueBuildResult result = builder.buildProjectWithResult(projectDir.toString());

        assertFalse(result.passed());
        assertTrue(result.durationMillis() >= 0);
        assertEquals("", result.outputSnippet());
        assertTrue(result.errorSnippet().contains("package.json 文件不存在"));
    }

    @Test
    void buildProject_usesStableResultForLegacyBoolean() throws Exception {
        Path projectDir = Files.createDirectory(tempDir.resolve("legacy-vue-project"));

        assertFalse(builder.buildProject(projectDir.toString()));
    }

    @Test
    void truncate_boundsLongSnippets() {
        String longOutput = "x".repeat(VueProjectBuilder.SNIPPET_MAX_CHARS + 10);

        String truncated = VueProjectBuilder.truncate(longOutput);

        assertTrue(truncated.length() <= VueProjectBuilder.SNIPPET_MAX_CHARS + "...[truncated]".length());
        assertTrue(truncated.endsWith("...[truncated]"));
    }
}
