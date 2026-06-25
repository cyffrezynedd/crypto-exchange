package com.exchange.jdbc.dao;

import com.exchange.jdbc.config.DbConnection;
import com.exchange.jdbc.model.Role;
import com.exchange.jdbc.model.UserRoleAssignment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserRoleDao {

    public List<Role> findAllRoles() throws SQLException {
        String sql = "SELECT id, code, name FROM roles ORDER BY id";
        List<Role> roles = new ArrayList<>();

        try (Connection conn = DbConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Role role = new Role();
                role.setId(rs.getLong("id"));
                role.setCode(rs.getString("code"));
                role.setName(rs.getString("name"));
                roles.add(role);
            }
        }
        return roles;
    }

    public List<UserRoleAssignment> findAllAssignments() throws SQLException {
        String sql = """
                SELECT ur.user_id, u.username, ur.role_id, r.code AS role_code, ur.assigned_at
                FROM user_roles ur
                JOIN users u ON u.id = ur.user_id
                JOIN roles r ON r.id = ur.role_id
                ORDER BY ur.user_id, ur.role_id
                """;

        List<UserRoleAssignment> assignments = new ArrayList<>();

        try (Connection conn = DbConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                assignments.add(mapAssignment(rs));
            }
        }
        return assignments;
    }

    public List<UserRoleAssignment> findByUserId(long userId) throws SQLException {
        String sql = """
                SELECT ur.user_id, u.username, ur.role_id, r.code AS role_code, ur.assigned_at
                FROM user_roles ur
                JOIN users u ON u.id = ur.user_id
                JOIN roles r ON r.id = ur.role_id
                WHERE ur.user_id = ?
                ORDER BY ur.role_id
                """;

        List<UserRoleAssignment> assignments = new ArrayList<>();

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    assignments.add(mapAssignment(rs));
                }
            }
        }
        return assignments;
    }

    public boolean assign(long userId, long roleId) throws SQLException {
        String sql = """
                INSERT INTO user_roles (user_id, role_id)
                VALUES (?, ?)
                ON CONFLICT (user_id, role_id) DO NOTHING
                """;

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setLong(2, roleId);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean remove(long userId, long roleId) throws SQLException {
        String sql = "DELETE FROM user_roles WHERE user_id = ? AND role_id = ?";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setLong(2, roleId);
            return ps.executeUpdate() > 0;
        }
    }

    public Optional<UserRoleAssignment> findAssignment(long userId, long roleId) throws SQLException {
        String sql = """
                SELECT ur.user_id, u.username, ur.role_id, r.code AS role_code, ur.assigned_at
                FROM user_roles ur
                JOIN users u ON u.id = ur.user_id
                JOIN roles r ON r.id = ur.role_id
                WHERE ur.user_id = ? AND ur.role_id = ?
                """;

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setLong(2, roleId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapAssignment(rs));
                }
            }
        }
        return Optional.empty();
    }

    private UserRoleAssignment mapAssignment(ResultSet rs) throws SQLException {
        UserRoleAssignment assignment = new UserRoleAssignment();
        assignment.setUserId(rs.getLong("user_id"));
        assignment.setUsername(rs.getString("username"));
        assignment.setRoleId(rs.getLong("role_id"));
        assignment.setRoleCode(rs.getString("role_code"));
        assignment.setAssignedAt(rs.getTimestamp("assigned_at").toInstant());
        return assignment;
    }
}
