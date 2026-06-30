package com.exchange.gateway.config;

import com.exchange.common.openapi.OpenApiTemplates;
import io.swagger.v3.oas.models.OpenAPI;
import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashSet;
import java.util.Set;

@Configuration
public class GatewayOpenApiConfig {

    @Bean
    public OpenAPI gatewayOpenAPI() {
        return OpenApiTemplates.forGateway();
    }

    @Bean
    public SwaggerUiConfigProperties swaggerUiConfigProperties() {
        SwaggerUiConfigProperties properties = new SwaggerUiConfigProperties();
        properties.setDisableSwaggerDefaultUrl(true);
        Set<SwaggerUiConfigProperties.SwaggerUrl> urls = new LinkedHashSet<>();
        urls.add(swaggerUrl("Gateway", "/v3/api-docs"));
        urls.add(swaggerUrl("IAM", "/api/v1/iam/v3/api-docs"));
        urls.add(swaggerUrl("Trading", "/api/v1/trading/v3/api-docs"));
        urls.add(swaggerUrl("Clearing", "/api/v1/clearing/v3/api-docs"));
        urls.add(swaggerUrl("Market Data", "/api/v1/market/v3/api-docs"));
        properties.setUrls(urls);
        return properties;
    }

    private static SwaggerUiConfigProperties.SwaggerUrl swaggerUrl(String name, String url) {
        SwaggerUiConfigProperties.SwaggerUrl entry = new SwaggerUiConfigProperties.SwaggerUrl();
        entry.setName(name);
        entry.setUrl(url);
        return entry;
    }
}
