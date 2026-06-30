package com.exchange.common.web;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@EnableConfigurationProperties(GatewayTrustProperties.class)
public class GatewayTrustAutoConfiguration {

    @Bean
    @ConditionalOnProperty(name = "exchange.gateway-trust.enabled", havingValue = "true")
    GatewayTrustFilter gatewayTrustFilter(GatewayTrustProperties properties) {
        return new GatewayTrustFilter(properties);
    }
}
