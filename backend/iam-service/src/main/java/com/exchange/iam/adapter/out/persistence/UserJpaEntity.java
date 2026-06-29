package com.exchange.iam.adapter.out.persistence;

import com.exchange.iam.domain.model.KycStatus;
import com.exchange.iam.domain.model.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;

@Entity
@Table(name = "users")
class UserJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(nullable = false, unique = true, length = 64)
    private String username;

    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "kyc_status", nullable = false, columnDefinition = "kyc_status")
    private KycStatus kycStatus;

    @Column(name = "is_active", nullable = false)
    private boolean active;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
    }

    static UserJpaEntity fromDomain(User user) {
        UserJpaEntity entity = new UserJpaEntity();
        entity.id = user.getId();
        entity.email = user.getEmail();
        entity.passwordHash = user.getPasswordHash();
        entity.username = user.getUsername();
        entity.kycStatus = user.getKycStatus();
        entity.active = user.isActive();
        entity.createdAt = user.getCreatedAt();
        entity.updatedAt = user.getUpdatedAt();
        return entity;
    }

    User toDomain() {
        User user = new User();
        user.setId(id);
        user.setEmail(email);
        user.setPasswordHash(passwordHash);
        user.setUsername(username);
        user.setKycStatus(kycStatus);
        user.setActive(active);
        user.setCreatedAt(createdAt);
        user.setUpdatedAt(updatedAt);
        return user;
    }
}
