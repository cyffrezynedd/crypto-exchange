package com.exchange.clearing.port.in;

import java.math.BigDecimal;
import java.util.UUID;

public record DepositCommand(
        UUID eventId,
        Long userId,
        Long currencyId,
        BigDecimal amount
) {
}
