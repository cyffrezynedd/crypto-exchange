CREATE TABLE orders (
    id                  UUID            PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id             BIGINT          NOT NULL REFERENCES users (id),
    trading_pair_id     BIGINT          NOT NULL REFERENCES trading_pairs (id),
    client_order_id     VARCHAR(64),
    side                order_side      NOT NULL,
    type                order_type      NOT NULL,
    price               NUMERIC(36, 18),
    quantity            NUMERIC(36, 18) NOT NULL,
    filled_quantity     NUMERIC(36, 18) NOT NULL DEFAULT 0,
    status              order_status    NOT NULL DEFAULT 'NEW',
    created_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW(),

    CONSTRAINT uq_orders_user_client_order UNIQUE (user_id, client_order_id),
    CONSTRAINT chk_orders_quantity_positive CHECK (quantity > 0),
    CONSTRAINT chk_orders_filled_quantity_range CHECK (
        filled_quantity >= 0 AND filled_quantity <= quantity
    ),
    CONSTRAINT chk_orders_limit_price_required CHECK (
        type = 'MARKET' OR (price IS NOT NULL AND price > 0)
    ),
    CONSTRAINT chk_orders_market_price_null CHECK (
        type = 'LIMIT' OR price IS NULL
    )
);

CREATE INDEX idx_orders_user_id ON orders (user_id);
CREATE INDEX idx_orders_trading_pair_id ON orders (trading_pair_id);
CREATE INDEX idx_orders_status ON orders (status);
CREATE INDEX idx_orders_created_at ON orders (created_at DESC);
CREATE INDEX idx_orders_pair_status_side_price ON orders (trading_pair_id, status, side, price);

CREATE TABLE trades (
    id                  UUID            PRIMARY KEY DEFAULT gen_random_uuid(),
    trading_pair_id     BIGINT          NOT NULL REFERENCES trading_pairs (id),
    buy_order_id        UUID            NOT NULL REFERENCES orders (id),
    sell_order_id       UUID            NOT NULL REFERENCES orders (id),
    buyer_id            BIGINT          NOT NULL REFERENCES users (id),
    seller_id           BIGINT          NOT NULL REFERENCES users (id),
    maker_order_id      UUID            NOT NULL REFERENCES orders (id),
    taker_order_id      UUID            NOT NULL REFERENCES orders (id),
    price               NUMERIC(36, 18) NOT NULL,
    quantity            NUMERIC(36, 18) NOT NULL,
    executed_at         TIMESTAMPTZ     NOT NULL DEFAULT NOW(),

    CONSTRAINT chk_trades_price_positive CHECK (price > 0),
    CONSTRAINT chk_trades_quantity_positive CHECK (quantity > 0),
    CONSTRAINT chk_trades_different_orders CHECK (buy_order_id <> sell_order_id)
);

CREATE INDEX idx_trades_trading_pair_id ON trades (trading_pair_id);
CREATE INDEX idx_trades_buy_order_id ON trades (buy_order_id);
CREATE INDEX idx_trades_sell_order_id ON trades (sell_order_id);
CREATE INDEX idx_trades_buyer_id ON trades (buyer_id);
CREATE INDEX idx_trades_seller_id ON trades (seller_id);
CREATE INDEX idx_trades_executed_at ON trades (executed_at DESC);
