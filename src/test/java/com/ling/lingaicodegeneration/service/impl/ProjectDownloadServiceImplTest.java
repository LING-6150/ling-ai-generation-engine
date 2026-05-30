package com.ling.lingaicodegeneration.service.impl;

import com.ling.lingaicodegeneration.exception.BusinessException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.junit.jupiter.api.Assertions.*;

class ProjectDownloadServiceImplTest {

    private final ProjectDownloadServiceImpl projectDownloadService = new ProjectDownloadServiceImpl();

    @TempDir
    Path tempDir;

    @Test
    void downloadProjectAsZipPackagesAllowedFiles() throws Exception {
        Path projectDir = tempDir.resolve("project");
        Files.createDirectories(projectDir.resolve("src"));
        Files.writeString(projectDir.resolve("src/App.vue"), "<template>Hello</template>");
        Files.writeString(projectDir.resolve("README.md"), "demo");

        MockHttpServletResponse response = new MockHttpServletResponse();
        projectDownloadService.downloadProjectAsZip(projectDir.toString(), "app-1", response);

        Set<String> entries = zipEntries(response.getContentAsByteArray());
        assertEquals(200, response.getStatus());
        assertEquals("application/zip", response.getContentType());
        assertEquals("attachment; filename=\"app-1.zip\"", response.getHeader("Content-Disposition"));
        assertTrue(entries.contains("README.md"));
        assertTrue(entries.contains("src/App.vue"));
    }

    @Test
    void downloadProjectAsZipPreservesIgnoredNamesAndExtensions() throws Exception {
        Path projectDir = tempDir.resolve("project");
        Files.createDirectories(projectDir.resolve("node_modules/pkg"));
        Files.createDirectories(projectDir.resolve("src"));
        Files.writeString(projectDir.resolve("src/index.js"), "console.log('ok')");
        Files.writeString(projectDir.resolve("src/app.log"), "ignore");
        Files.writeString(projectDir.resolve(".env"), "SECRET=value");
        Files.writeString(projectDir.resolve("node_modules/pkg/index.js"), "ignore");

        MockHttpServletResponse response = new MockHttpServletResponse();
        projectDownloadService.downloadProjectAsZip(projectDir.toString(), "app-2", response);

        Set<String> entries = zipEntries(response.getContentAsByteArray());
        assertEquals(Set.of("src/index.js"), entries);
    }

    @Test
    void downloadProjectAsZipRejectsProjectsOverFileCountLimit() throws Exception {
        Path projectDir = tempDir.resolve("project");
        Files.createDirectories(projectDir);
        for (int i = 0; i < 1_001; i++) {
            Files.writeString(projectDir.resolve("file-%04d.txt".formatted(i)), "x");
        }

        BusinessException exception = assertThrows(BusinessException.class,
                () -> projectDownloadService.downloadProjectAsZip(
                        projectDir.toString(), "too-many", new MockHttpServletResponse()));

        assertTrue(exception.getMessage().contains("too many files"));
    }

    @Test
    void downloadProjectAsZipRejectsProjectsOverByteLimit() throws Exception {
        Path projectDir = tempDir.resolve("project");
        Files.createDirectories(projectDir);
        byte[] oversizedContent = new byte[50 * 1024 * 1024 + 1];
        Files.write(projectDir.resolve("large.bin"), oversizedContent);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> projectDownloadService.downloadProjectAsZip(
                        projectDir.toString(), "too-large", new MockHttpServletResponse()));

        assertTrue(exception.getMessage().contains("too large"));
    }

    @Test
    void downloadProjectAsZipSkipsSymbolicLinks() throws Exception {
        Path projectDir = tempDir.resolve("project");
        Path outsideFile = tempDir.resolve("outside-secret.txt");
        Files.createDirectories(projectDir);
        Files.writeString(projectDir.resolve("safe.txt"), "safe");
        Files.writeString(outsideFile, "secret");
        Path link = projectDir.resolve("secret-link.txt");
        try {
            Files.createSymbolicLink(link, outsideFile);
        } catch (UnsupportedOperationException | IOException e) {
            return;
        }

        MockHttpServletResponse response = new MockHttpServletResponse();
        projectDownloadService.downloadProjectAsZip(projectDir.toString(), "app-3", response);

        Set<String> entries = zipEntries(response.getContentAsByteArray());
        assertEquals(Set.of("safe.txt"), entries);
    }

    private Set<String> zipEntries(byte[] zipBytes) throws IOException {
        Set<String> entries = new HashSet<>();
        try (ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(zipBytes), StandardCharsets.UTF_8)) {
            ZipEntry zipEntry;
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                entries.add(zipEntry.getName());
            }
        }
        return entries;
    }
}
