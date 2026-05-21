package com.ling.lingaicodegeneration.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ImageSearchExecutorConfig {

    @Bean(name = "imageSearchExecutor", destroyMethod = "shutdown")
    public ExecutorService imageSearchExecutor() {
        return Executors.newFixedThreadPool(3);
    }
}
