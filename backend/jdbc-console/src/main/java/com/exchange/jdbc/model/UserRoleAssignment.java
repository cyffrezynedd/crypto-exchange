package com.exchange.jdbc.model;

import java.time.Instant;

public class UserRoleAssignment {

    private long userId;
    private String username;
    private long roleId;
    private String roleCode;
    private Instant assignedAt;

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getRoleId() {
        return roleId;
    }

    public void setRoleId(long roleId) {
        this.roleId = roleId;
    }

    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }

    public Instant getAssignedAt() {
        return assignedAt;
    }

    public void setAssignedAt(Instant assignedAt) {
        this.assignedAt = assignedAt;
    }

    @Override
    public String toString() {
        return "UserRole{userId=" + userId
                + ", username='" + username + '\''
                + ", roleId=" + roleId
                + ", roleCode='" + roleCode + '\''
                + ", assignedAt=" + assignedAt + '}';
    }
}
