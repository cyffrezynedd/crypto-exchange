package com.exchange.jdbc.dao;

import com.exchange.jdbc.config.DbConnection;
import com.exchange.jdbc.model.Currency;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CurrencyDao {

    private Currency mapRow(ResultSet rs) throws SQLException {
        Currency currency = new Currency();
        currency.setId(rs.getLong("id"));
        currency.setCode(rs.getString("code"));
        currency.setName(rs.getString("name"));
        currency.setDecimals(rs.getShort("decimals"));
        currency.setActive(rs.getBoolean("is_active"));
        currency.setCreatedAt(rs.getTimestamp("created_at").toInstant());
        return currency;
    }

    public Currency create(Currency currency) throws SQLException {
        String sql = """
                INSERT INTO currencies (code, name, decimals, is_active)
                VALUES (?, ?, ?, ?)
                RETURNING id, created_at
                """;

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, currency.getCode());
            ps.setString(2, currency.getName());
            ps.setShort(3, currency.getDecimals());
            ps.setBoolean(4, currency.isActive());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    currency.setId(rs.getLong("id"));
                    currency.setCreatedAt(rs.getTimestamp("created_at").toInstant());
                }
            }
        }
        return currency;
    }

    public List<Currency> findAll() throws SQLException {
        String sql = "SELECT * FROM currencies ORDER BY id";
        List<Currency> currencies = new ArrayList<>();

        try (Connection conn = DbConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                currencies.add(mapRow(rs));
            }
        }
        return currencies;
    }

    public Optional<Currency> findById(long id) throws SQLException {
        String sql = "SELECT * FROM currencies WHERE id = ?";

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

    public boolean update(Currency currency) throws SQLException {
        String sql = """
                UPDATE currencies
                SET code = ?, name = ?, decimals = ?, is_active = ?
                WHERE id = ?
                """;

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, currency.getCode());
            ps.setString(2, currency.getName());
            ps.setShort(3, currency.getDecimals());
            ps.setBoolean(4, currency.isActive());
            ps.setLong(5, currency.getId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(long id) throws SQLException {
        String sql = "DELETE FROM currencies WHERE id = ?";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        }
    }
}
