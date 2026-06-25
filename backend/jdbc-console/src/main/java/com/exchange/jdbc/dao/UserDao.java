package com.exchange.jdbc.dao;

import com.exchange.jdbc.config.DbConnection;
import com.exchange.jdbc.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDao {

    private User mapRow(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setEmail(rs.getString("email"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setUsername(rs.getString("username"));
        user.setKycStatus(rs.getString("kyc_status"));
        user.setActive(rs.getBoolean("is_active"));
        user.setCreatedAt(rs.getTimestamp("created_at").toInstant());
        user.setUpdatedAt(rs.getTimestamp("updated_at").toInstant());
        return user;
    }

    public User create(User user) throws SQLException {
        String sql = """
                INSERT INTO users (email, password_hash, username, kyc_status, is_active)
                VALUES (?, ?, ?, ?::kyc_status, ?)
                RETURNING id, created_at, updated_at
                """;

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getPasswordHash());
            ps.setString(3, user.getUsername());
            ps.setString(4, user.getKycStatus());
            ps.setBoolean(5, user.isActive());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    user.setId(rs.getLong("id"));
                    user.setCreatedAt(rs.getTimestamp("created_at").toInstant());
                    user.setUpdatedAt(rs.getTimestamp("updated_at").toInstant());
                }
            }
        }
        return user;
    }

    public List<User> findAll() throws SQLException {
        String sql = "SELECT * FROM users ORDER BY id";
        List<User> users = new ArrayList<>();

        try (Connection conn = DbConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                users.add(mapRow(rs));
            }
        }
        return users;
    }

    public Optional<User> findById(long id) throws SQLException {
        String sql = "SELECT * FROM users WHERE id = ?";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        }
        return Optional.empty();
    }

    public boolean update(User user) throws SQLException {
        String sql = """
                UPDATE users
                SET email = ?, password_hash = ?, username = ?, kyc_status = ?::kyc_status,
                    is_active = ?, updated_at = ?
                WHERE id = ?
                """;

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getPasswordHash());
            ps.setString(3, user.getUsername());
            ps.setString(4, user.getKycStatus());
            ps.setBoolean(5, user.isActive());
            ps.setTimestamp(6, Timestamp.from(Instant.now()));
            ps.setLong(7, user.getId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(long id) throws SQLException {
        String sql = "DELETE FROM users WHERE id = ?";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        }
    }
}
