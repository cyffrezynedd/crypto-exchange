package com.exchange.trading.adapter.in.web.dto;

import jakarta.validation.constraints.NotNull;

public record AddFavoritePairRequest(
        @NotNull Long tradingPairId
) {
}
