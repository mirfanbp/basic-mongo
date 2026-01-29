package com.exercise.mongo.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;

@Configuration
@EnableCaching // for redis
public class RedisConfig {
    private static final Duration SHORT_TTL = Duration.ofSeconds(15);
    private static final Duration LONG_TTL = Duration.ofMinutes(10);

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory, ObjectMapper objectMapper) {

        RedisSerializationContext.SerializationPair<Object> jsonSerializer =
                RedisSerializationContext.SerializationPair.fromSerializer(
                        RedisSerializer.json()
                );

        RedisCacheConfiguration baseConfig =
                RedisCacheConfiguration.defaultCacheConfig()
                        .serializeValuesWith(jsonSerializer)
                        .disableCachingNullValues();

        return RedisCacheManager.builder(connectionFactory)
                .withCacheConfiguration(
                        "enrollments",
                        baseConfig.entryTtl(SHORT_TTL)
                )
                .withCacheConfiguration(
                        "students",
                        baseConfig.entryTtl(LONG_TTL)
                )
                .build();
    }



}

