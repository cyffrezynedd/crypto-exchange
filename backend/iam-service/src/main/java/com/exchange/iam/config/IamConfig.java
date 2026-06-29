package com.exchange.iam.config;

import com.exchange.common.security.JwtTokenProvider;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableConfigurationProperties(JwtProperties.class)
public class IamConfig {

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    JwtTokenProvider jwtTokenProvider(JwtProperties properties) {
        return new JwtTokenProvider(
                properties.secret(),
                properties.accessTtlSeconds(),
                properties.refreshTtlSeconds()
        );
    }
}
