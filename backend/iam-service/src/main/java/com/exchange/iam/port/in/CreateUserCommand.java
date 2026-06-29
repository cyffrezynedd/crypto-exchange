package com.exchange.iam.port.in;

import com.exchange.iam.domain.model.KycStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateUserCommand(
        @NotBlank @Email String email,
        @NotBlank @Size(min = 8, max = 72) String password,
        @NotBlank @Size(max = 64) String username,
        KycStatus kycStatus,
        Boolean active
) {
    public CreateUserCommand {
        if (kycStatus == null) {
            kycStatus = KycStatus.PENDING;
        }
        if (active == null) {
            active = true;
        }
    }
}
