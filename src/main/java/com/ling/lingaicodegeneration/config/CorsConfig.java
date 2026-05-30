package com.ling.lingaicodegeneration.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * 全局跨域问题解决
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Value("${app.cors.allowed-origins:http://localhost:5173,http://127.0.0.1:5173}")
    private List<String> allowedOrigins;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        String[] origins = allowedOrigins.stream()
                .map(String::trim)
                .filter(origin -> !origin.isBlank())
                .toArray(String[]::new);

        registry.addMapping("/**")
                .allowCredentials(true)
                .allowedOrigins(origins)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("*");
    }
}
