package com.exchange.gateway.filter;

import org.junit.jupiter.api.Test;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class InternalApiBlockFilterTest {

    private final InternalApiBlockFilter filter = new InternalApiBlockFilter();

    @Test
    void blocksInternalClearingPaths() {
        ServerWebExchange exchange = exchangeFor("POST", "/api/v1/clearing/internal/v1/funds/freeze");

        filter.filter(exchange, ex -> Mono.empty()).block();

        assertEquals(404, exchange.getResponse().getStatusCode().value());
    }

    @Test
    void allowsPublicWalletPaths() {
        ServerWebExchange exchange = exchangeFor("GET", "/api/v1/clearing/wallets");

        filter.filter(exchange, ex -> {
            assertNull(ex.getResponse().getStatusCode());
            return Mono.empty();
        }).block();
    }

    private static ServerWebExchange exchangeFor(String method, String path) {
        MockServerHttpRequest request = MockServerHttpRequest.method(
                org.springframework.http.HttpMethod.valueOf(method), path).build();
        return MockServerWebExchange.from(request);
    }
}
