package com.pereira.api.shared.config;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 *
 * @author Jose Luis Pereira
 */
@ConfigurationProperties(prefix = "app.pago")
public record PagoProperties(String baseUrl, Duration connectTimeout, Duration readTimeout) {

}
