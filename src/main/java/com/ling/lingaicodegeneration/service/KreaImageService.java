package com.ling.lingaicodegeneration.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ling.lingaicodegeneration.constant.AppConstant;
import com.ling.lingaicodegeneration.exception.BusinessException;
import com.ling.lingaicodegeneration.exception.ErrorCode;
import com.ling.lingaicodegeneration.model.enums.CodeGenTypeEnum;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Base64;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Semaphore;

@Service
@ConditionalOnProperty(name = "app.images.krea.enabled", havingValue = "true")
public class KreaImageService {
    private static final int MAX_RESPONSE_BYTES = 16 * 1024 * 1024;
    private final Semaphore gpu = new Semaphore(1);
    private final ObjectMapper json;
    private final URI endpoint;
    private final String model;
    private final String key;
    private final String size;

    public KreaImageService(ObjectMapper json, @Value("${app.images.krea.base-url}") String baseUrl,
                            @Value("${app.images.krea.model}") String model,
                            @Value("${app.images.krea.api-key:}") String key,
                            @Value("${app.images.krea.size}") String size) {
        this.json = json;
        this.endpoint = URI.create(baseUrl.replaceAll("/+$", "") + "/images/generations");
        this.model = model;
        this.key = key;
        this.size = size;
    }

    public String generate(long appId, CodeGenTypeEnum type, String prompt) {
        if (prompt == null || prompt.isBlank() || prompt.length() > 2000) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Provide an image description of 1–2000 characters");
        }
        if (!gpu.tryAcquire()) throw new BusinessException(ErrorCode.TOO_MANY_REQUEST, "Image generator is busy");
        try {
            BufferedImage image = requestImage(prompt);
            Path root = Path.of(AppConstant.CODE_OUTPUT_ROOT_DIR);
            Files.createDirectories(root);
            Path project = directory(root.toRealPath(), type.getValue() + "_" + appId);
            Path publicRoot = type == CodeGenTypeEnum.VUE_PROJECT ? directory(project, "public") : project;
            Path assets = directory(publicRoot, "assets");
            String name = UUID.randomUUID() + ".png";
            Path temporary = Files.createTempFile(assets, ".render-", ".tmp");
            try {
                if (!ImageIO.write(image, "png", temporary.toFile())) throw new IOException("PNG encoder unavailable");
                Files.move(temporary, assets.resolve(name), StandardCopyOption.ATOMIC_MOVE);
            } finally {
                Files.deleteIfExists(temporary);
            }
            return "assets/" + name;
        } catch (IOException | IllegalArgumentException e) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "Krea image generation or asset storage failed");
        } finally {
            gpu.release();
        }
    }

    private Path directory(Path parent, String name) throws IOException {
        Path child = parent.resolve(name);
        Files.createDirectories(child);
        if (!child.equals(child.toRealPath())) throw new IOException("Unexpected project directory");
        return child;
    }

    private BufferedImage requestImage(String prompt) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) endpoint.toURL().openConnection();
        try {
            connection.setRequestMethod("POST");
            connection.setInstanceFollowRedirects(false);
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(180_000);
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");
            if (!key.isBlank()) connection.setRequestProperty("Authorization", "Bearer " + key);
            try (var body = connection.getOutputStream()) {
                json.writeValue(body, Map.of("model", model, "prompt", prompt, "n", 1,
                        "size", size, "response_format", "b64_json"));
            }
            if (connection.getResponseCode() != 200) throw new IOException("Image provider rejected request");
            byte[] response;
            try (var body = connection.getInputStream()) {
                response = body.readNBytes(MAX_RESPONSE_BYTES + 1);
            }
            if (response.length > MAX_RESPONSE_BYTES) throw new IOException("Image response too large");
            String encoded = json.readTree(response).at("/data/0/b64_json").asText("");
            return decodeImage(Base64.getDecoder().decode(encoded));
        } finally {
            connection.disconnect();
        }
    }

    private BufferedImage decodeImage(byte[] bytes) throws IOException {
        try (ImageInputStream input = ImageIO.createImageInputStream(new ByteArrayInputStream(bytes))) {
            Iterator<ImageReader> readers = ImageIO.getImageReaders(input);
            if (!readers.hasNext()) throw new IOException("Invalid image");
            ImageReader reader = readers.next();
            try {
                reader.setInput(input, true, true);
                if ((long) reader.getWidth(0) * reader.getHeight(0) > 4096L * 4096) {
                    throw new IOException("Image dimensions too large");
                }
                return reader.read(0);
            } finally {
                reader.dispose();
            }
        }
    }
}
