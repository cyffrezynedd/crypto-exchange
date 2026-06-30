package com.exchange.trading.adapter.out.clearing;

import com.exchange.common.clearing.FreezeFundsRequest;
import com.exchange.common.clearing.FreezeFundsResponse;
import com.exchange.common.clearing.SettleTradeRequest;
import com.exchange.common.clearing.UnfreezeFundsRequest;
import com.exchange.common.error.ErrorCode;
import com.exchange.common.error.ExchangeException;
import com.exchange.common.web.GatewayHeaders;
import com.exchange.trading.config.ClearingProperties;
import com.exchange.trading.domain.model.Trade;
import com.exchange.trading.domain.model.TradingPair;
import com.exchange.trading.port.out.ClearingPort;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.UUID;

@Component
class ClearingWebClientAdapter implements ClearingPort {

    private static final String FREEZE_PATH = "/internal/v1/funds/freeze";
    private static final String UNFREEZE_PATH = "/internal/v1/funds/unfreeze";
    private static final String SETTLE_PATH = "/internal/v1/trades/settle";

    private final WebClient webClient;
    private final String internalApiKey;

    ClearingWebClientAdapter(WebClient.Builder webClientBuilder, ClearingProperties properties) {
        this.webClient = webClientBuilder.baseUrl(properties.getBaseUrl()).build();
        this.internalApiKey = properties.getInternalApiKey();
    }

    @Override
    public void freezeFunds(UUID orderId, Long userId, Long currencyId, BigDecimal amount) {
        FreezeFundsRequest request = new FreezeFundsRequest(
                orderId.toString(),
                userId,
                currencyId,
                amount,
                "ORDER_LOCK"
        );

        FreezeFundsResponse response = post(FREEZE_PATH, request, FreezeFundsResponse.class);
        if (response == null || !response.success()) {
            throw new ExchangeException(ErrorCode.INSUFFICIENT_FUNDS, "Insufficient funds to place order");
        }
    }

    @Override
    public void unfreezeFunds(UUID orderId, Long userId, Long currencyId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }
        UnfreezeFundsRequest request = new UnfreezeFundsRequest(
                "unfreeze-" + orderId,
                userId,
                currencyId,
                amount,
                "ORDER_UNLOCK"
        );
        post(UNFREEZE_PATH, request, Object.class);
    }

    @Override
    public void settleTrade(Trade trade, TradingPair pair) {
        SettleTradeRequest request = new SettleTradeRequest(
                trade.getId(),
                trade.getId(),
                trade.getBuyerId(),
                trade.getSellerId(),
                pair.getQuoteCurrencyId(),
                pair.getBaseCurrencyId(),
                trade.getPrice(),
                trade.getQuantity(),
                null
        );
        post(SETTLE_PATH, request, Object.class);
    }

    private <T> T post(String path, Object body, Class<T> responseType) {
        return webClient.post()
                .uri(path)
                .headers(headers -> {
                    if (internalApiKey != null && !internalApiKey.isBlank()) {
                        headers.set(GatewayHeaders.INTERNAL_API_HEADER, internalApiKey);
                    }
                })
                .bodyValue(body)
                .retrieve()
                .onStatus(HttpStatusCode::isError, clientResponse -> clientResponse.bodyToMono(String.class)
                        .defaultIfEmpty("Clearing service error")
                        .flatMap(message -> Mono.error(new ExchangeException(
                                ErrorCode.SERVICE_UNAVAILABLE,
                                "Clearing request failed: " + message
                        ))))
                .bodyToMono(responseType)
                .block();
    }
}
