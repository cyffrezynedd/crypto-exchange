package com.exchange.gateway.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "gateway.jwt")
public record GatewayJwtProperties(
        String secret,
        long accessTtlSeconds,
        long refreshTtlSeconds
) {
    public GatewayJwtProperties {
        if (secret == null || secret.isBlank()) {
            secret = "dev-only-change-me-exchange-jwt-secret-key-32chars";
        }
        if (accessTtlSeconds <= 0) {
            accessTtlSeconds = 900;
        }
        if (refreshTtlSeconds <= 0) {
            refreshTtlSeconds = 604800;
        }
    }
}
