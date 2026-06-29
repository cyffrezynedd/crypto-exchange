package com.exchange.gateway.handler;

import com.exchange.gateway.config.GatewayApiPaths;
import com.exchange.gateway.config.GatewayProperties;
import com.exchange.gateway.model.GatewayInfoResponse;
import com.exchange.gateway.model.ServiceStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/gateway")
public class GatewayInfoController {

    private final WebClient webClient;
    private final GatewayProperties properties;
    private final String applicationName;
    private final String applicationVersion;

    public GatewayInfoController(
            WebClient.Builder webClientBuilder,
            GatewayProperties properties,
            @Value("${spring.application.name}") String applicationName,
            @Value("${spring.application.version:1.0.0}") String applicationVersion) {
        this.webClient = webClientBuilder.build();
        this.properties = properties;
        this.applicationName = applicationName;
        this.applicationVersion = applicationVersion;
    }

    @GetMapping("/info")
    public Mono<GatewayInfoResponse> info() {
        List<Mono<ServiceStatus>> checks = List.of(
                checkHealth("iam", GatewayApiPaths.IAM, properties.getServices().getIamUri()),
                checkHealth("clearing", GatewayApiPaths.CLEARING, properties.getServices().getClearingUri()),
                checkHealth("trading", GatewayApiPaths.TRADING, properties.getServices().getTradingUri()),
                checkHealth("market-data", GatewayApiPaths.MARKET, properties.getServices().getMarketUri())
        );

        return Flux.concat(checks)
                .collectList()
                .map(statuses -> new GatewayInfoResponse(
                        applicationName,
                        applicationVersion,
                        GatewayApiPaths.VERSION,
                        statuses));
    }

    private Mono<ServiceStatus> checkHealth(String name, String path, String baseUri) {
        String healthUrl = baseUri + "/actuator/health";

        return webClient.get()
                .uri(healthUrl)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(java.util.Map.class)
                .map(body -> {
                    Object status = body.get("status");
                    String detail = status != null ? status.toString() : "UNKNOWN";
                    return new ServiceStatus(name, path, "UP", detail);
                })
                .onErrorResume(ex -> Mono.just(
                        new ServiceStatus(name, path, "DOWN", ex.getMessage())));
    }
}
