package com.exchange.gateway.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class FallbackController {

    @RequestMapping("/fallback/iam")
    public ResponseEntity<Map<String, String>> iamFallback() {
        return fallback("iam");
    }

    @RequestMapping("/fallback/trading")
    public ResponseEntity<Map<String, String>> tradingFallback() {
        return fallback("trading");
    }

    @RequestMapping("/fallback/clearing")
    public ResponseEntity<Map<String, String>> clearingFallback() {
        return fallback("clearing");
    }

    @RequestMapping("/fallback/market")
    public ResponseEntity<Map<String, String>> marketFallback() {
        return fallback("market-data");
    }

    private static ResponseEntity<Map<String, String>> fallback(String service) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(Map.of(
                "service", service,
                "status", "CIRCUIT_OPEN",
                "message", service + " service is temporarily unavailable"
        ));
    }
}
