package com.ling.lingaicodegeneration.config;


import dev.langchain4j.community.store.memory.chat.redis.RedisChatMemoryStore;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "spring.data.redis")
public class RedisChatMemoryStoreConfig {

    private String host;

    private int port;

    private String password;

    private long ttl;

    @Bean
    public RedisChatMemoryStore redisChatMemoryStore() {
        RedisChatMemoryStore.Builder builder = RedisChatMemoryStore.builder()
                .host(host)
                .port(port)
                .ttl(ttl);
        // 如果密码不为空才设置
        if (password != null && !password.isBlank()) {
            builder.user(password);
        }
        return builder.build();
    }
}