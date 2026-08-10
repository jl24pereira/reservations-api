package com.pereira.api.shared.config;

import java.time.Duration;

import com.github.benmanes.caffeine.cache.Caffeine;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author Jose Luis Pereira
 */
@Configuration
@EnableCaching
public class CacheConfig {

    public static final String CACHE_OCUPACION = "ocupacion";

    @Bean
    CacheManager cacheManager() {
        CaffeineCacheManager manager = new CaffeineCacheManager(CACHE_OCUPACION);

        manager.setCaffeine(Caffeine.newBuilder()
                .maximumSize(200)
                .expireAfterWrite(Duration.ofMinutes(10))
                .recordStats());

        return manager;
    }

}
