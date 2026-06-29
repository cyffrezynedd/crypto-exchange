package com.exchange.iam.adapter.in.web.dto;

import com.exchange.iam.port.in.AuthTokens;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank @Email String email,
        @NotBlank String password
) {
}
