-- NFC Payment System Database Schema
-- Database: nfc_payment_system_db

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
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
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

-- INDEXES FOR PERFORMANCE
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

-- TRIGGER FOR AUTOMATIC WALLET TIMESTAMP UPDATE
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