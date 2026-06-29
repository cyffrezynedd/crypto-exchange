SELECT 'iam.users' AS table_name, COUNT(*) AS row_count FROM iam.users
UNION ALL
SELECT 'market.currencies', COUNT(*) FROM market.currencies
UNION ALL
SELECT 'market.trading_pairs', COUNT(*) FROM market.trading_pairs;
