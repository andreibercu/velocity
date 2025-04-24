package com.abercu.velocity.birds.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Profile("!test")
@Configuration
public class RedisCacheConfig {

    public static final String BIRDS_CACHE = "birds";
    public static final String BIRDS_SEARCH_CACHE = "birdsSearch";
    public static final String SIGHTINGS_CACHE = "sightings";
    public static final String SIGHTINGS_SEARCH_CACHE = "sightingsSearch";

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10)) // default
                .disableCachingNullValues();

        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();

        // use different TTLs for specific caches
        cacheConfigurations.put(BIRDS_CACHE, defaultConfig.entryTtl(Duration.ofMinutes(30)));
        cacheConfigurations.put(BIRDS_SEARCH_CACHE, defaultConfig.entryTtl(Duration.ofMinutes(10)));

        cacheConfigurations.put(SIGHTINGS_CACHE, defaultConfig.entryTtl(Duration.ofMinutes(30)));
        cacheConfigurations.put(SIGHTINGS_SEARCH_CACHE, defaultConfig.entryTtl(Duration.ofMinutes(10)));

        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigurations)
                .build();
    }
}
