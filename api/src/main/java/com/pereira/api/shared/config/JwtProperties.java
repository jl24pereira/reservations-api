package com.pereira.api.shared.config;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 *
 * @author Jose Luis Pereira
 */
@ConfigurationProperties(prefix = "app.security.jwt")
public record JwtProperties(String secret, Duration expiration) {

}
