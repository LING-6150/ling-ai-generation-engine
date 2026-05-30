package com.ling.lingaicodegeneration.service.impl;

import com.ling.lingaicodegeneration.exception.BusinessException;
import com.ling.lingaicodegeneration.exception.ErrorCode;
import com.ling.lingaicodegeneration.exception.ThrowUtils;
import com.ling.lingaicodegeneration.service.ProjectDownloadService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.*;
import java.util.Comparator;
import java.util.Set;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
@Service
public class ProjectDownloadServiceImpl implements ProjectDownloadService {

    private static final int MAX_FILE_COUNT = 1_000;
    private static final long MAX_TOTAL_BYTES = 50L * 1024 * 1024;

    private static final Set<String> IGNORED_NAMES = Set.of(
            "node_modules", ".git", "dist", "build",
            ".DS_Store", ".env", "target", ".mvn", ".idea", ".vscode"
    );

    private static final Set<String> IGNORED_EXTENSIONS = Set.of(
            ".log", ".tmp", ".cache"
    );

    @Override
    public void downloadProjectAsZip(String projectPath, String downloadFileName, HttpServletResponse response) {
        ThrowUtils.throwIf(projectPath == null || projectPath.isBlank(),
                ErrorCode.PARAMS_ERROR, "Project path cannot be empty");
        ThrowUtils.throwIf(downloadFileName == null || downloadFileName.isBlank(),
                ErrorCode.PARAMS_ERROR, "Download file name cannot be empty");

        Path projectRootPath = Paths.get(projectPath).toAbsolutePath().normalize();
        ThrowUtils.throwIf(!Files.exists(projectRootPath), ErrorCode.NOT_FOUND_ERROR, "Project directory not found");
        ThrowUtils.throwIf(!Files.isDirectory(projectRootPath), ErrorCode.PARAMS_ERROR, "Specified path is not a directory");

        log.info("Starting ZIP download: {} -> {}.zip", projectPath, downloadFileName);

        // Write ZIP into memory first, then flush to response in one shot
        // This prevents stream corruption from Spring filters or exception handlers
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipBuildStats stats = new ZipBuildStats();

        try (ZipOutputStream zos = new ZipOutputStream(baos);
             Stream<Path> paths = Files.walk(projectRootPath)) {
            paths
                    .sorted(Comparator.naturalOrder())
                    .filter(path -> Files.isRegularFile(path, LinkOption.NOFOLLOW_LINKS))
                    .filter(path -> isPathAllowed(projectRootPath, path))
                    .forEach(filePath -> {
                        try {
                            long fileSize = Files.size(filePath);
                            stats.addFile(fileSize);
                            String entryName = toSafeZipEntryName(projectRootPath, filePath);
                            zos.putNextEntry(new ZipEntry(entryName));
                            Files.copy(filePath, zos);
                            zos.closeEntry();
                        } catch (IOException e) {
                            throw new UncheckedIOException(e);
                        }
                    });
        } catch (UncheckedIOException | IOException e) {
            log.error("Failed to build ZIP for: {}", projectPath, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Failed to package project: " + e.getMessage());
        }

        byte[] zipBytes = baos.toByteArray();
        log.info("ZIP built in memory: {} bytes, files={}, rawBytes={}",
                zipBytes.length, stats.fileCount, stats.totalBytes);

        // Set headers and write bytes to response
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/zip");
        response.setContentLength(zipBytes.length);
        response.addHeader("Content-Disposition",
                String.format("attachment; filename=\"%s.zip\"", downloadFileName));
        response.addHeader("Access-Control-Expose-Headers", "Content-Disposition");

        try (OutputStream os = response.getOutputStream()) {
            os.write(zipBytes);
            os.flush();
        } catch (IOException e) {
            log.error("Failed to write ZIP to response", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Failed to send file");
        }

        log.info("ZIP download completed: {}.zip", downloadFileName);
    }

    private boolean isPathAllowed(Path projectRoot, Path fullPath) {
        Path normalizedPath = fullPath.toAbsolutePath().normalize();
        if (!normalizedPath.startsWith(projectRoot)) {
            log.warn("Skipping file outside project root: {}", fullPath);
            return false;
        }

        Path relativePath = projectRoot.relativize(normalizedPath);
        if (relativePath.isAbsolute() || relativePath.normalize().startsWith("..")) {
            log.warn("Skipping unsafe ZIP path: {}", fullPath);
            return false;
        }

        for (Path part : relativePath) {
            String partName = part.toString();
            if (partName.isBlank() || ".".equals(partName) || "..".equals(partName)) {
                log.warn("Skipping non-normalized ZIP path: {}", fullPath);
                return false;
            }
            if (IGNORED_NAMES.contains(partName)) {
                return false;
            }
            if (IGNORED_EXTENSIONS.stream().anyMatch(partName::endsWith)) {
                return false;
            }
        }
        return true;
    }

    private String toSafeZipEntryName(Path projectRoot, Path filePath) {
        Path normalizedPath = filePath.toAbsolutePath().normalize();
        Path relativePath = projectRoot.relativize(normalizedPath);
        String entryName = relativePath.toString().replace(File.separatorChar, '/');
        if (entryName.isBlank() || entryName.startsWith("/")
                || entryName.equals("..") || entryName.startsWith("../") || entryName.contains("/../")) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Unsafe ZIP entry path: " + entryName);
        }
        return entryName;
    }

    private static class ZipBuildStats {

        private int fileCount;

        private long totalBytes;

        private void addFile(long fileSize) {
            fileCount++;
            totalBytes += fileSize;
            if (fileCount > MAX_FILE_COUNT) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR,
                        "Project has too many files to download as ZIP");
            }
            if (totalBytes > MAX_TOTAL_BYTES) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR,
                        "Project is too large to download as ZIP");
            }
        }
    }
}
