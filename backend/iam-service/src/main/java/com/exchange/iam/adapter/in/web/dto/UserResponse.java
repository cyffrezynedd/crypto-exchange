package com.exchange.iam.adapter.in.web.dto;

import com.exchange.iam.domain.model.KycStatus;
import com.exchange.iam.domain.model.User;
import com.exchange.iam.port.in.CreateUserCommand;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public record UserResponse(
        Long id,
        String email,
        String username,
        KycStatus kycStatus,
        boolean active,
        Instant createdAt,
        Instant updatedAt
) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                user.getKycStatus(),
                user.isActive(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
