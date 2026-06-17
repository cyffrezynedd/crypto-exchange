CREATE TABLE currencies (
    id              BIGSERIAL       PRIMARY KEY,
    code            VARCHAR(16)     NOT NULL,
    name            VARCHAR(64)     NOT NULL,
    decimals        SMALLINT        NOT NULL,
    is_active       BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),

    CONSTRAINT uq_currencies_code UNIQUE (code),
    CONSTRAINT chk_currencies_decimals CHECK (decimals >= 0 AND decimals <= 18)
);

CREATE TABLE trading_pairs (
    id                  BIGSERIAL       PRIMARY KEY,
    symbol              VARCHAR(32)     NOT NULL,
    base_currency_id    BIGINT          NOT NULL REFERENCES currencies (id),
    quote_currency_id   BIGINT          NOT NULL REFERENCES currencies (id),
    min_order_amount    NUMERIC(36, 18) NOT NULL DEFAULT 0,
    tick_size           NUMERIC(36, 18) NOT NULL DEFAULT 0.01,
    lot_size            NUMERIC(36, 18) NOT NULL DEFAULT 0.00000001,
    is_active           BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW(),

    CONSTRAINT uq_trading_pairs_symbol UNIQUE (symbol),
    CONSTRAINT chk_trading_pairs_different_currencies CHECK (base_currency_id <> quote_currency_id),
    CONSTRAINT chk_trading_pairs_min_order_nonneg CHECK (min_order_amount >= 0),
    CONSTRAINT chk_trading_pairs_tick_size_positive CHECK (tick_size > 0),
    CONSTRAINT chk_trading_pairs_lot_size_positive CHECK (lot_size > 0)
);

CREATE INDEX idx_trading_pairs_base_currency ON trading_pairs (base_currency_id);
CREATE INDEX idx_trading_pairs_quote_currency ON trading_pairs (quote_currency_id);

CREATE TABLE user_favorite_pairs (
    user_id             BIGINT          NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    trading_pair_id     BIGINT          NOT NULL REFERENCES trading_pairs (id) ON DELETE CASCADE,
    added_at            TIMESTAMPTZ     NOT NULL DEFAULT NOW(),

    PRIMARY KEY (user_id, trading_pair_id)
);

CREATE INDEX idx_user_favorite_pairs_trading_pair ON user_favorite_pairs (trading_pair_id);
