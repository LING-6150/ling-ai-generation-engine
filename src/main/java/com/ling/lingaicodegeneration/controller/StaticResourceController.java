package com.ling.lingaicodegeneration.controller;

import com.ling.lingaicodegeneration.constant.AppConstant;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;

@RestController
@RequestMapping("/static")
public class StaticResourceController {

    // App code output root dir (for preview)
    private static final String PREVIEW_ROOT_DIR = AppConstant.CODE_OUTPUT_ROOT_DIR;

    /**
     * Serve static resources, support directory redirect
     * Access pattern: http://localhost:8123/api/static/{codeGenType}_{appId}/{fileName}
     */
    @GetMapping("/{dirName}/**")
    public ResponseEntity<Resource> serveStaticResource(
            @PathVariable String dirName,
            HttpServletRequest request) {
        try {
            // Get resource path
            String resourcePath = (String) request.getAttribute(
                    "jakarta.servlet.forward.request_uri");
            if (resourcePath == null) {
                resourcePath = request.getRequestURI();
            }
            resourcePath = resourcePath.substring(
                    ("/api/static/" + dirName).length());

            // If directory access, redirect to index.html
            if (resourcePath.isEmpty()) {
                HttpHeaders headers = new HttpHeaders();
                headers.add("Location", request.getRequestURI() + "/");
                return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
            }

            // Default to index.html
            if (resourcePath.equals("/")) {
                resourcePath = "/index.html";
            }

            // Build file path
            String filePath = PREVIEW_ROOT_DIR + "/" + dirName + resourcePath;
            File file = new File(filePath);

            // Check if file exists
            if (!file.exists()) {
                return ResponseEntity.notFound().build();
            }

            // Return file resource
            Resource resource = new FileSystemResource(file);
            return ResponseEntity.ok()
                    .header("Content-Type", getContentTypeWithCharset(filePath))
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get content type based on file extension
     */
    private String getContentTypeWithCharset(String filePath) {
        if (filePath.endsWith(".html")) return "text/html; charset=UTF-8";
        if (filePath.endsWith(".css")) return "text/css; charset=UTF-8";
        if (filePath.endsWith(".js")) return "application/javascript; charset=UTF-8";
        if (filePath.endsWith(".png")) return "image/png";
        if (filePath.endsWith(".jpg")) return "image/jpeg";
        return "application/octet-stream";
    }
}