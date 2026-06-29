package com.exchange.iam.adapter.in.web.dto;

import com.exchange.iam.port.in.AuthTokens;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        long expiresInSeconds,
        Long userId,
        String username
) {
    public static AuthResponse from(AuthTokens tokens) {
        return new AuthResponse(
                tokens.accessToken(),
                tokens.refreshToken(),
                tokens.expiresInSeconds(),
                tokens.userId(),
                tokens.username()
        );
    }
}
