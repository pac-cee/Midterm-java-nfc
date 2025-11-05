-- Sample Data for NFC Payment System

-- Sample Merchants (Pre-loaded for testing)
INSERT INTO merchants (merchant_name, merchant_code, category) VALUES
('Starbucks Coffee', 'STBK001', 'Food & Beverage'),
('Amazon Store', 'AMZN001', 'E-commerce'),
('Shell Gas Station', 'SHLL001', 'Fuel'),
('McDonald''s Restaurant', 'MCDO001', 'Fast Food'),
('Walmart Supermarket', 'WLMT001', 'Retail'),
('Netflix Streaming', 'NFLX001', 'Entertainment'),
('Uber Ride', 'UBER001', 'Transportation'),
('Apple Store', 'APPL001', 'Electronics'),
('KFC Restaurant', 'KFCR001', 'Fast Food'),
('Spotify Music', 'SPOT001', 'Entertainment');

-- Sample Test User (Password: test123)
-- Note: In real implementation, password will be BCrypt hashed
INSERT INTO users (full_name, email, password_hash, phone) VALUES
('Test User', 'test@nfcpay.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye', '+250788123456');

-- Sample Wallet for Test User
INSERT INTO wallets (user_id, balance, currency) VALUES
(1, 1000.00, 'USD');

-- Sample Cards for Test User
INSERT INTO cards (user_id, card_uid, card_name, card_type) VALUES
(1, 'A1B2C3D4-E5F6-7890-ABCD-EF1234567890', 'Primary Card', 'VIRTUAL'),
(1, 'B2C3D4E5-F6G7-8901-BCDE-F12345678901', 'Backup Card', 'PHYSICAL');

-- Sample Transactions
INSERT INTO transactions (user_id, card_id, merchant_id, amount, status, reference_code, description) VALUES
(1, 1, 1, 15.75, 'SUCCESS', 'TXN1234567890ABCDEF', 'Coffee and pastry'),
(1, 1, 4, 8.50, 'SUCCESS', 'TXN2345678901BCDEFG', 'Big Mac meal'),
(1, 2, 2, 45.99, 'SUCCESS', 'TXN3456789012CDEFGH', 'Book purchase'),
(1, 1, 3, 65.00, 'SUCCESS', 'TXN4567890123DEFGHI', 'Gas fill-up');