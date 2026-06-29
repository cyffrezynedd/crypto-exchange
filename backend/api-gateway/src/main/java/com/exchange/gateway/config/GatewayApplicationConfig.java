package com.exchange.gateway.config;

import com.exchange.common.security.JwtTokenProvider;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({GatewayProperties.class, GatewayJwtProperties.class})
public class GatewayApplicationConfig {

    @Bean
    JwtTokenProvider jwtTokenProvider(GatewayJwtProperties properties) {
        return new JwtTokenProvider(
                properties.secret(),
                properties.accessTtlSeconds(),
                properties.refreshTtlSeconds()
        );
    }
}
