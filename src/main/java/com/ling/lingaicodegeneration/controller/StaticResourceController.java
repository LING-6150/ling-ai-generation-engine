package com.ling.lingaicodegeneration.controller;

import com.ling.lingaicodegeneration.constant.AppConstant;
import com.ling.lingaicodegeneration.constant.UserConstant;
import com.ling.lingaicodegeneration.model.entity.App;
import com.ling.lingaicodegeneration.model.entity.User;
import com.ling.lingaicodegeneration.model.enums.CodeGenTypeEnum;
import com.ling.lingaicodegeneration.service.AppService;
import com.ling.lingaicodegeneration.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequiredArgsConstructor
public class StaticResourceController {
    private static final Pattern DIRECTORY = Pattern.compile("(html|multi_file|vue_project)_([1-9][0-9]{0,18})");
    private static final String TICKET_PREFIX = "preview:ticket:";
    private final AppService apps;
    private final UserService users;
    private final StringRedisTemplate redis;

    @Value("${app.preview.base-url:}")
    private String previewBaseUrl;

    @Value("${app.preview.frame-origin:'self'}")
    private String frameOrigin;

    @GetMapping("/static/{dirName}/{*file}")
    public ResponseEntity<Void> openPreview(@PathVariable String dirName, @PathVariable String file,
                                            HttpServletRequest request) {
        Matcher directory = DIRECTORY.matcher(dirName);
        if (!directory.matches()) return ResponseEntity.notFound().build();
        long id;
        try {
            id = Long.parseLong(directory.group(2));
        } catch (NumberFormatException e) {
            return ResponseEntity.notFound().build();
        }
        User user = users.getLoginUser(request);
        App app = apps.getById(id);
        if (app == null || !directory.group(1).equals(app.getCodeGenType())) return ResponseEntity.notFound().build();
        if (!user.getId().equals(app.getUserId()) && !UserConstant.ADMIN_ROLE.equals(user.getUserRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        boolean vue = CodeGenTypeEnum.VUE_PROJECT.getValue().equals(app.getCodeGenType());
        String entry = file.isEmpty() || file.equals("/") ? "/index.html" : file;
        if (vue && entry.startsWith("/dist/")) entry = entry.substring(5);
        String directoryName = vue ? dirName + "/dist" : dirName;
        if (!safeFile(entry)) return ResponseEntity.notFound().build();
        String ticket = UUID.randomUUID().toString();
        redis.opsForValue().set(TICKET_PREFIX + ticket, directoryName, Duration.ofMinutes(5));
        String base = previewBaseUrl.isBlank() ? request.getContextPath() + "/preview" : previewBaseUrl;
        URI target = UriComponentsBuilder.fromUriString(base).pathSegment(ticket).path(entry).build().encode().toUri();
        return ResponseEntity.status(HttpStatus.FOUND).location(target).cacheControl(CacheControl.noStore())
                .header("Referrer-Policy", "no-referrer").build();
    }

    @GetMapping("/preview/{ticket}/{*file}")
    public ResponseEntity<Resource> readPreview(@PathVariable UUID ticket, @PathVariable String file) {
        if (!safeFile(file)) return ResponseEntity.notFound().build();
        String directory = redis.opsForValue().get(TICKET_PREFIX + ticket);
        if (directory == null) return ResponseEntity.notFound().build();
        try {
            Path root = Path.of(AppConstant.CODE_OUTPUT_ROOT_DIR).toRealPath().resolve(directory);
            Path candidate = root.resolve(file.substring(1));
            // Reject links in every component, including the app directory itself.
            if (!candidate.toRealPath().equals(candidate) || !Files.isRegularFile(candidate)) {
                return ResponseEntity.notFound().build();
            }
            Resource resource = new FileSystemResource(candidate);
            String mediaType = MediaTypeFactory.getMediaType(resource)
                    .map(Object::toString).orElse("application/octet-stream");
            if (mediaType.startsWith("text/") || mediaType.equals("application/javascript")) {
                mediaType += "; charset=UTF-8";
            }
            return ResponseEntity.ok().cacheControl(CacheControl.noStore())
                    .header("Content-Type", mediaType)
                    .header("X-Content-Type-Options", "nosniff")
                    .header("Referrer-Policy", "no-referrer")
                    .header("Content-Security-Policy", "sandbox allow-scripts; default-src 'self' data: blob:; "
                            + "script-src 'self' 'unsafe-inline'; style-src 'self' 'unsafe-inline'; "
                            + "connect-src 'none'; object-src 'none'; form-action 'none'; base-uri 'none'; "
                            + "frame-ancestors " + frameOrigin)
                    .header("Access-Control-Allow-Origin", "*")
                    .body(resource);
        } catch (IOException e) {
            return ResponseEntity.notFound().build();
        }
    }

    private static boolean safeFile(String file) {
        if (!file.startsWith("/") || file.length() < 2) return false;
        for (String part : file.substring(1).split("/", -1)) {
            if (part.isBlank() || part.startsWith(".") || part.indexOf('\\') >= 0 || part.indexOf(':') >= 0) {
                return false;
            }
        }
        return true;
    }
}
