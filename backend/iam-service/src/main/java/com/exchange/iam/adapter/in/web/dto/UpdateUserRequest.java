package com.exchange.iam.adapter.in.web.dto;

import com.exchange.iam.domain.model.KycStatus;
import com.exchange.iam.port.in.UpdateUserCommand;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateUserRequest(
        @NotBlank @Email String email,
        @Size(min = 8, max = 72) String password,
        @NotBlank @Size(max = 64) String username,
        @NotNull KycStatus kycStatus,
        @NotNull Boolean active
) {
    public UpdateUserCommand toCommand(Long id) {
        return new UpdateUserCommand(id, email, password, username, kycStatus, active);
    }
}
