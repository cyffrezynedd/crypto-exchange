package com.exchange.clearing;

import com.exchange.clearing.config.ClearingFeaturesProperties;
import com.exchange.clearing.config.InternalApiProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({ClearingFeaturesProperties.class, InternalApiProperties.class})
public class ClearingServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClearingServiceApplication.class, args);
    }
}
