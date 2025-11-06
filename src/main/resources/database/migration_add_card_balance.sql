-- Migration script to add balance column to cards table
-- Run this if you have existing cards without balance column

-- Add balance column to cards table
ALTER TABLE cards ADD COLUMN IF NOT EXISTS balance DECIMAL(10,2) DEFAULT 0.00 CHECK (balance >= 0);

-- Update existing cards to have zero balance
UPDATE cards SET balance = 0.00 WHERE balance IS NULL;

-- Add comment for documentation
COMMENT ON COLUMN cards.balance IS 'Card balance in the default currency';