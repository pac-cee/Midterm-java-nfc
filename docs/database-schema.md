# 🗄️ Database Schema Documentation

## 1. Database Overview

**Database Name**: `nfc_payment_system_db`  
**DBMS**: PostgreSQL 15+  
**Character Set**: UTF-8  
**Collation**: en_US.UTF-8  

## 2. Schema Design

### 2.1 Tables Summary

| Table | Purpose | Records | Relationships |
|-------|---------|---------|---------------|
| users | User accounts and authentication | ~1000 | 1:1 wallets, 1:N cards, 1:N transactions |
| wallets | User balance management | ~1000 | N:1 users |
| cards | NFC card information | ~5000 | N:1 users, 1:N transactions |
| merchants | Payment recipients | ~100 | 1:N transactions |
| transactions | Payment records | ~50000 | N:1 users, N:1 cards, N:1 merchants |

### 2.2 Complete Schema SQL

```sql
-- Database Creation
CREATE DATABASE nfc_payment_system_db
    WITH ENCODING 'UTF8'
    LC_COLLATE = 'en_US.UTF-8'
    LC_CTYPE = 'en_US.UTF-8';

-- Connect to database
\c nfc_payment_system_db;

-- Enable UUID extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- 1. USERS TABLE
CREATE TABLE users (
    user_id SERIAL PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL CHECK (LENGTH(full_name) >= 2),
    email VARCHAR(100) UNIQUE NOT NULL CHECK (email ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$'),
    password_hash VARCHAR(255) NOT NULL,
    phone VARCHAR(15) CHECK (phone ~* '^\+?[1-9]\d{1,14}$'),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP,
    
    CONSTRAINT users_email_lowercase CHECK (email = LOWER(email))
);

-- 2. WALLETS TABLE
CREATE TABLE wallets (
    wallet_id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    balance DECIMAL(10,2) DEFAULT 0.00 CHECK (balance >= 0),
    currency VARCHAR(3) DEFAULT 'USD' CHECK (currency IN ('USD', 'EUR', 'GBP', 'RWF')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT unique_user_wallet UNIQUE (user_id)
);

-- 3. CARDS TABLE
CREATE TABLE cards (
    card_id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    card_uid VARCHAR(50) UNIQUE NOT NULL,
    card_name VARCHAR(50) NOT NULL CHECK (LENGTH(card_name) >= 2),
    card_type VARCHAR(20) DEFAULT 'VIRTUAL' CHECK (card_type IN ('VIRTUAL', 'PHYSICAL')),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT unique_card_uid CHECK (card_uid ~* '^[A-F0-9]{8}-[A-F0-9]{4}-[A-F0-9]{4}-[A-F0-9]{4}-[A-F0-9]{12}$')
);

-- 4. MERCHANTS TABLE
CREATE TABLE merchants (
    merchant_id SERIAL PRIMARY KEY,
    merchant_name VARCHAR(100) NOT NULL CHECK (LENGTH(merchant_name) >= 2),
    merchant_code VARCHAR(20) UNIQUE NOT NULL,
    category VARCHAR(50) DEFAULT 'General',
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT merchant_code_format CHECK (merchant_code ~* '^[A-Z0-9]{4,10}$')
);

-- 5. TRANSACTIONS TABLE
CREATE TABLE transactions (
    transaction_id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL REFERENCES users(user_id),
    card_id INTEGER NOT NULL REFERENCES cards(card_id),
    merchant_id INTEGER NOT NULL REFERENCES merchants(merchant_id),
    amount DECIMAL(10,2) NOT NULL CHECK (amount > 0 AND amount <= 10000),
    transaction_type VARCHAR(20) DEFAULT 'PAYMENT' CHECK (transaction_type IN ('PAYMENT', 'REFUND')),
    status VARCHAR(20) DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'SUCCESS', 'FAILED', 'CANCELLED')),
    reference_code VARCHAR(50) UNIQUE NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    processed_at TIMESTAMP,
    
    CONSTRAINT reference_code_format CHECK (reference_code ~* '^TXN[A-Z0-9]{10,20}$')
);

-- 6. INDEXES FOR PERFORMANCE
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_active ON users(is_active);
CREATE INDEX idx_cards_user_id ON cards(user_id);
CREATE INDEX idx_cards_uid ON cards(card_uid);
CREATE INDEX idx_cards_active ON cards(is_active);
CREATE INDEX idx_transactions_user_id ON transactions(user_id);
CREATE INDEX idx_transactions_card_id ON transactions(card_id);
CREATE INDEX idx_transactions_merchant_id ON transactions(merchant_id);
CREATE INDEX idx_transactions_status ON transactions(status);
CREATE INDEX idx_transactions_created_at ON transactions(created_at);
CREATE INDEX idx_transactions_reference ON transactions(reference_code);
CREATE INDEX idx_wallets_user_id ON wallets(user_id);

-- 7. TRIGGERS FOR AUTOMATIC UPDATES
CREATE OR REPLACE FUNCTION update_wallet_timestamp()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER wallet_update_trigger
    BEFORE UPDATE ON wallets
    FOR EACH ROW
    EXECUTE FUNCTION update_wallet_timestamp();

-- 8. VIEWS FOR COMMON QUERIES
CREATE VIEW user_card_summary AS
SELECT 
    u.user_id,
    u.full_name,
    u.email,
    COUNT(c.card_id) as total_cards,
    COUNT(CASE WHEN c.is_active THEN 1 END) as active_cards,
    w.balance,
    w.currency
FROM users u
LEFT JOIN cards c ON u.user_id = c.user_id
LEFT JOIN wallets w ON u.user_id = w.user_id
WHERE u.is_active = TRUE
GROUP BY u.user_id, u.full_name, u.email, w.balance, w.currency;

CREATE VIEW transaction_summary AS
SELECT 
    t.transaction_id,
    u.full_name as user_name,
    c.card_name,
    m.merchant_name,
    t.amount,
    t.status,
    t.created_at,
    t.reference_code
FROM transactions t
JOIN users u ON t.user_id = u.user_id
JOIN cards c ON t.card_id = c.card_id
JOIN merchants m ON t.merchant_id = m.merchant_id
ORDER BY t.created_at DESC;
```

## 3. Table Specifications

### 3.1 users Table

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| user_id | SERIAL | PRIMARY KEY | Auto-incrementing user identifier |
| full_name | VARCHAR(100) | NOT NULL, LENGTH >= 2 | User's full name |
| email | VARCHAR(100) | UNIQUE, NOT NULL, EMAIL FORMAT | User's email address |
| password_hash | VARCHAR(255) | NOT NULL | BCrypt hashed password |
| phone | VARCHAR(15) | PHONE FORMAT | User's phone number |
| is_active | BOOLEAN | DEFAULT TRUE | Account status |
| created_at | TIMESTAMP | DEFAULT NOW() | Account creation time |
| last_login | TIMESTAMP | NULL | Last login timestamp |

**Business Rules:**
- Email must be unique and lowercase
- Full name minimum 2 characters
- Phone number must follow international format
- Password stored as BCrypt hash only

### 3.2 wallets Table

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| wallet_id | SERIAL | PRIMARY KEY | Auto-incrementing wallet identifier |
| user_id | INTEGER | FK to users, NOT NULL | Wallet owner |
| balance | DECIMAL(10,2) | DEFAULT 0.00, >= 0 | Current balance |
| currency | VARCHAR(3) | DEFAULT 'USD' | Currency code |
| created_at | TIMESTAMP | DEFAULT NOW() | Wallet creation time |
| updated_at | TIMESTAMP | DEFAULT NOW() | Last balance update |

**Business Rules:**
- One wallet per user (unique constraint)
- Balance cannot be negative
- Supported currencies: USD, EUR, GBP, RWF
- Auto-update timestamp on balance changes

### 3.3 cards Table

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| card_id | SERIAL | PRIMARY KEY | Auto-incrementing card identifier |
| user_id | INTEGER | FK to users, NOT NULL | Card owner |
| card_uid | VARCHAR(50) | UNIQUE, NOT NULL, UUID FORMAT | NFC card unique identifier |
| card_name | VARCHAR(50) | NOT NULL, LENGTH >= 2 | User-defined card name |
| card_type | VARCHAR(20) | DEFAULT 'VIRTUAL' | Card type (VIRTUAL/PHYSICAL) |
| is_active | BOOLEAN | DEFAULT TRUE | Card status |
| created_at | TIMESTAMP | DEFAULT NOW() | Card creation time |

**Business Rules:**
- Card UID must follow UUID format
- Card name minimum 2 characters
- Users can have multiple cards
- Only active cards can process payments

### 3.4 merchants Table

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| merchant_id | SERIAL | PRIMARY KEY | Auto-incrementing merchant identifier |
| merchant_name | VARCHAR(100) | NOT NULL, LENGTH >= 2 | Merchant business name |
| merchant_code | VARCHAR(20) | UNIQUE, NOT NULL | Merchant identifier code |
| category | VARCHAR(50) | DEFAULT 'General' | Business category |
| is_active | BOOLEAN | DEFAULT TRUE | Merchant status |
| created_at | TIMESTAMP | DEFAULT NOW() | Registration time |

**Business Rules:**
- Merchant code must be alphanumeric, 4-10 characters
- Merchant name minimum 2 characters
- Only active merchants can receive payments

### 3.5 transactions Table

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| transaction_id | SERIAL | PRIMARY KEY | Auto-incrementing transaction identifier |
| user_id | INTEGER | FK to users, NOT NULL | Transaction initiator |
| card_id | INTEGER | FK to cards, NOT NULL | Card used for payment |
| merchant_id | INTEGER | FK to merchants, NOT NULL | Payment recipient |
| amount | DECIMAL(10,2) | NOT NULL, > 0, <= 10000 | Transaction amount |
| transaction_type | VARCHAR(20) | DEFAULT 'PAYMENT' | Transaction type |
| status | VARCHAR(20) | DEFAULT 'PENDING' | Transaction status |
| reference_code | VARCHAR(50) | UNIQUE, NOT NULL | Unique transaction reference |
| description | TEXT | NULL | Optional transaction description |
| created_at | TIMESTAMP | DEFAULT NOW() | Transaction initiation time |
| processed_at | TIMESTAMP | NULL | Transaction completion time |

**Business Rules:**
- Amount must be positive and <= $10,000
- Reference code format: TXN + 10-20 alphanumeric characters
- Status progression: PENDING → SUCCESS/FAILED/CANCELLED
- All foreign keys must reference active records

## 4. Relationships

### 4.1 Primary Relationships

```sql
-- One-to-One: User ↔ Wallet
users.user_id ←→ wallets.user_id (UNIQUE)

-- One-to-Many: User → Cards
users.user_id ←→ cards.user_id (MULTIPLE)

-- One-to-Many: User → Transactions
users.user_id ←→ transactions.user_id (MULTIPLE)

-- One-to-Many: Card → Transactions
cards.card_id ←→ transactions.card_id (MULTIPLE)

-- One-to-Many: Merchant → Transactions
merchants.merchant_id ←→ transactions.merchant_id (MULTIPLE)
```

### 4.2 Referential Integrity

```sql
-- Cascade Deletes
DELETE FROM users → CASCADE DELETE wallets, cards
DELETE FROM cards → RESTRICT (if transactions exist)
DELETE FROM merchants → RESTRICT (if transactions exist)

-- Foreign Key Constraints
ALTER TABLE wallets ADD CONSTRAINT fk_wallet_user 
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE;

ALTER TABLE cards ADD CONSTRAINT fk_card_user 
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE;

ALTER TABLE transactions ADD CONSTRAINT fk_transaction_user 
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE RESTRICT;

ALTER TABLE transactions ADD CONSTRAINT fk_transaction_card 
    FOREIGN KEY (card_id) REFERENCES cards(card_id) ON DELETE RESTRICT;

ALTER TABLE transactions ADD CONSTRAINT fk_transaction_merchant 
    FOREIGN KEY (merchant_id) REFERENCES merchants(merchant_id) ON DELETE RESTRICT;
```

## 5. Sample Data

### 5.1 Initial Setup Data

```sql
-- Sample Users
INSERT INTO users (full_name, email, password_hash, phone) VALUES
('John Doe', 'john.doe@email.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye', '+1234567890'),
('Jane Smith', 'jane.smith@email.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye', '+0987654321'),
('Pacific Tester', 'pacific@test.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye', '+250788123456');

-- Sample Wallets (auto-created via trigger or application)
INSERT INTO wallets (user_id, balance, currency) VALUES
(1, 1000.00, 'USD'),
(2, 750.50, 'USD'),
(3, 2500.00, 'USD');

-- Sample Merchants
INSERT INTO merchants (merchant_name, merchant_code, category) VALUES
('Starbucks Coffee', 'STBK001', 'Food & Beverage'),
('Amazon Store', 'AMZN001', 'E-commerce'),
('Shell Gas Station', 'SHLL001', 'Fuel'),
('McDonald''s Restaurant', 'MCDO001', 'Fast Food'),
('Walmart Supermarket', 'WLMT001', 'Retail'),
('Netflix Streaming', 'NFLX001', 'Entertainment');

-- Sample Cards
INSERT INTO cards (user_id, card_uid, card_name, card_type) VALUES
(1, 'A1B2C3D4-E5F6-7890-ABCD-EF1234567890', 'Primary Card', 'VIRTUAL'),
(1, 'B2C3D4E5-F6G7-8901-BCDE-F12345678901', 'Backup Card', 'PHYSICAL'),
(2, 'C3D4E5F6-G7H8-9012-CDEF-123456789012', 'Main Card', 'VIRTUAL'),
(3, 'D4E5F6G7-H8I9-0123-DEF1-234567890123', 'Test Card', 'VIRTUAL');

-- Sample Transactions
INSERT INTO transactions (user_id, card_id, merchant_id, amount, status, reference_code, description) VALUES
(1, 1, 1, 15.75, 'SUCCESS', 'TXN1234567890ABCDEF', 'Coffee and pastry'),
(1, 1, 4, 8.50, 'SUCCESS', 'TXN2345678901BCDEFG', 'Big Mac meal'),
(2, 3, 2, 45.99, 'SUCCESS', 'TXN3456789012CDEFGH', 'Book purchase'),
(3, 4, 3, 65.00, 'SUCCESS', 'TXN4567890123DEFGHI', 'Gas fill-up');
```

## 6. Performance Optimization

### 6.1 Indexing Strategy

```sql
-- Primary indexes (automatic)
-- Secondary indexes for frequent queries
CREATE INDEX CONCURRENTLY idx_users_email_active ON users(email, is_active);
CREATE INDEX CONCURRENTLY idx_cards_user_active ON cards(user_id, is_active);
CREATE INDEX CONCURRENTLY idx_transactions_user_date ON transactions(user_id, created_at DESC);
CREATE INDEX CONCURRENTLY idx_transactions_status_date ON transactions(status, created_at DESC);

-- Composite indexes for complex queries
CREATE INDEX CONCURRENTLY idx_transactions_search ON transactions(user_id, status, created_at DESC);
```

### 6.2 Query Optimization

```sql
-- Efficient user dashboard query
SELECT 
    u.full_name,
    w.balance,
    COUNT(c.card_id) as card_count,
    COUNT(t.transaction_id) as transaction_count
FROM users u
LEFT JOIN wallets w ON u.user_id = w.user_id
LEFT JOIN cards c ON u.user_id = c.user_id AND c.is_active = TRUE
LEFT JOIN transactions t ON u.user_id = t.user_id AND t.created_at >= CURRENT_DATE - INTERVAL '30 days'
WHERE u.user_id = ? AND u.is_active = TRUE
GROUP BY u.user_id, u.full_name, w.balance;

-- Efficient transaction history with pagination
SELECT 
    t.transaction_id,
    t.amount,
    t.status,
    t.created_at,
    m.merchant_name,
    c.card_name
FROM transactions t
JOIN merchants m ON t.merchant_id = m.merchant_id
JOIN cards c ON t.card_id = c.card_id
WHERE t.user_id = ?
ORDER BY t.created_at DESC
LIMIT 20 OFFSET ?;
```

This database schema provides a solid foundation for the NFC Payment System with proper constraints, relationships, and performance optimizations.