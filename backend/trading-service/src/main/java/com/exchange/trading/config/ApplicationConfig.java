package com.exchange.trading.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@EnableConfigurationProperties({ClearingProperties.class, TradingProperties.class})
public class ApplicationConfig {

    @Bean
    WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }
}
