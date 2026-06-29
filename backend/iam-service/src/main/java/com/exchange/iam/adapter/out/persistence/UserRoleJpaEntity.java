package com.exchange.iam.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "user_roles")
class UserRoleJpaEntity {

    @EmbeddedId
    private UserRoleId id;

    @Column(name = "assigned_at", nullable = false)
    private Instant assignedAt;

    static UserRoleJpaEntity of(Long userId, Long roleId) {
        UserRoleJpaEntity entity = new UserRoleJpaEntity();
        entity.id = new UserRoleId(userId, roleId);
        entity.assignedAt = Instant.now();
        return entity;
    }

    @Embeddable
    static class UserRoleId implements Serializable {
        @Column(name = "user_id")
        private Long userId;

        @Column(name = "role_id")
        private Long roleId;

        protected UserRoleId() {
        }

        UserRoleId(Long userId, Long roleId) {
            this.userId = userId;
            this.roleId = roleId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof UserRoleId that)) {
                return false;
            }
            return Objects.equals(userId, that.userId) && Objects.equals(roleId, that.roleId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(userId, roleId);
        }
    }
}
