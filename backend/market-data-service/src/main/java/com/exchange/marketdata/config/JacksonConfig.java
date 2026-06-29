package com.exchange.marketdata.config;

import com.exchange.common.json.ExchangeObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    @Bean
    ObjectMapper objectMapper() {
        return ExchangeObjectMapper.create();
    }
}
