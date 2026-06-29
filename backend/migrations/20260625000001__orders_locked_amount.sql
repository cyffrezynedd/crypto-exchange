ALTER TABLE trading.orders
    ADD COLUMN IF NOT EXISTS locked_amount NUMERIC(36, 18);

UPDATE trading.orders
SET locked_amount = quantity
WHERE locked_amount IS NULL AND side = 'SELL';

UPDATE trading.orders
SET locked_amount = price * quantity
WHERE locked_amount IS NULL AND side = 'BUY' AND type = 'LIMIT';
