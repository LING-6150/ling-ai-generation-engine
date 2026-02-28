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
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
@Service
public class ProjectDownloadServiceImpl implements ProjectDownloadService {

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

        File projectDir = new File(projectPath);
        ThrowUtils.throwIf(!projectDir.exists(), ErrorCode.NOT_FOUND_ERROR, "Project directory not found");
        ThrowUtils.throwIf(!projectDir.isDirectory(), ErrorCode.PARAMS_ERROR, "Specified path is not a directory");

        log.info("Starting ZIP download: {} -> {}.zip", projectPath, downloadFileName);

        // Write ZIP into memory first, then flush to response in one shot
        // This prevents stream corruption from Spring filters or exception handlers
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Path projectRootPath = projectDir.toPath();

        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            Files.walk(projectRootPath)
                    .filter(path -> !Files.isDirectory(path))
                    .filter(path -> isPathAllowed(projectRootPath, path))
                    .forEach(filePath -> {
                        String entryName = projectRootPath.relativize(filePath).toString();
                        try {
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
        log.info("ZIP built in memory: {} bytes", zipBytes.length);

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
        Path relativePath = projectRoot.relativize(fullPath);
        for (Path part : relativePath) {
            String partName = part.toString();
            if (IGNORED_NAMES.contains(partName)) {
                return false;
            }
            if (IGNORED_EXTENSIONS.stream().anyMatch(partName::endsWith)) {
                return false;
            }
        }
        return true;
    }
}