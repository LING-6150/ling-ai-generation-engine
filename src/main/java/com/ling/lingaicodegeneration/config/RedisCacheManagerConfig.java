package com.ling.lingaicodegeneration.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.annotation.Resource;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

/**
 * Redis Cache Manager configuration.
 * Enables Spring Cache annotations (@Cacheable, @CacheEvict, etc.)
 * backed by Redis for distributed caching.
 *
 * Key design decisions:
 * 1. StringRedisSerializer for keys — keeps Redis keys human-readable
 * 2. GenericJackson2JsonRedisSerializer for values — supports complex objects
 * 3. JavaTimeModule registered — handles Java 8+ time types (LocalDateTime etc.)
 * 4. Default TTL: 30 minutes; featured apps cache: 5 minutes (updated less frequently)
 * 5. disableCachingNullValues() — don't cache null results
 */
@EnableCaching
@Configuration
public class RedisCacheManagerConfig {

    @Resource
    private RedisConnectionFactory redisConnectionFactory;

    @Bean
    public CacheManager cacheManager() {
        // Configure ObjectMapper to support Java 8 time types
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        // Default cache configuration
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(30))             // Default TTL: 30 min
                .disableCachingNullValues()                   // Don't cache nulls
                // key: String serialization (human-readable in Redis)
                .serializeKeysWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new StringRedisSerializer()))
                // value: JSON serialization (supports complex objects)
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new GenericJackson2JsonRedisSerializer(objectMapper)));

        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(defaultConfig)
                // Featured apps list: shorter TTL (5 min) — admins update it occasionally
                .withCacheConfiguration("good_app_page",
                        defaultConfig.entryTtl(Duration.ofMinutes(5)))
                .build();
    }
}