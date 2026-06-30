package com.exchange.clearing.config;

import com.exchange.common.openapi.OpenApiTemplates;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return OpenApiTemplates.forService("Clearing Service", "/api/v1/clearing");
    }
}
