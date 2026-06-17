INSERT INTO roles (code, name, description) VALUES
    ('USER', 'User', 'Пользователь'),
    ('ADMIN', 'Admin', 'Администратор');

INSERT INTO currencies (code, name, decimals) VALUES
    ('BTC', 'Bitcoin', 8),
    ('ETH', 'Ethereum', 8),
    ('USDT', 'Tether', 6);

INSERT INTO trading_pairs (symbol, base_currency_id, quote_currency_id, min_order_amount, tick_size, lot_size)
SELECT 'BTC_USDT', b.id, q.id, 0.0001, 0.01, 0.00000001
FROM currencies b, currencies q
WHERE b.code = 'BTC' AND q.code = 'USDT';

INSERT INTO trading_pairs (symbol, base_currency_id, quote_currency_id, min_order_amount, tick_size, lot_size)
SELECT 'ETH_USDT', b.id, q.id, 0.001, 0.01, 0.00000001
FROM currencies b, currencies q
WHERE b.code = 'ETH' AND q.code = 'USDT';
