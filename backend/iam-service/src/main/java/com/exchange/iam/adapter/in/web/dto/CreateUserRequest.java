package com.exchange.iam.adapter.in.web.dto;

import com.exchange.iam.domain.model.KycStatus;
import com.exchange.iam.port.in.CreateUserCommand;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateUserRequest(
        @NotBlank @Email String email,
        @NotBlank @Size(min = 8, max = 72) String password,
        @NotBlank @Size(max = 64) String username,
        KycStatus kycStatus,
        Boolean active
) {
    public CreateUserCommand toCommand() {
        return new CreateUserCommand(email, password, username, kycStatus, active);
    }
}
