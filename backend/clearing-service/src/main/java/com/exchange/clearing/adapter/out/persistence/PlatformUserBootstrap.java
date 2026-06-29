package com.exchange.clearing.adapter.out.persistence;

import com.exchange.clearing.application.ClearingProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class PlatformUserBootstrap implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(PlatformUserBootstrap.class);

    private final JdbcTemplate jdbcTemplate;
    private final ClearingProperties properties;

    public PlatformUserBootstrap(JdbcTemplate jdbcTemplate, ClearingProperties properties) {
        this.jdbcTemplate = jdbcTemplate;
        this.properties = properties;
    }

    @Override
    public void run(ApplicationArguments args) {
        long platformUserId = properties.platformUserId();
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM iam.users WHERE id = ?",
                Integer.class,
                platformUserId);
        if (count != null && count > 0) {
            return;
        }

        jdbcTemplate.update("""
                INSERT INTO iam.users (id, email, password_hash, username, kyc_status, is_active)
                VALUES (?, 'platform@system.local', 'N/A', 'platform', 'VERIFIED', TRUE)
                ON CONFLICT (id) DO NOTHING
                """, platformUserId);

        jdbcTemplate.execute("""
                SELECT setval(
                    pg_get_serial_sequence('iam.users', 'id'),
                    GREATEST((SELECT COALESCE(MAX(id), 1) FROM iam.users), 1)
                )
                """);

        log.info("Ensured platform treasury user exists: id={}", platformUserId);
    }
}
