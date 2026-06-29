package com.exchange.clearing.adapter.in.web.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record DepositRequest(
        UUID eventId,
        @NotNull Long currencyId,
        @NotNull @DecimalMin(value = "0.000000000000000001", inclusive = true) BigDecimal amount
) {
}
