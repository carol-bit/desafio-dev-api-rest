-- =============================================
-- MIGRATION: Initial creation of Dock Digital tables
-- =============================================

-- =======================
-- Holder Table
-- =======================
CREATE TABLE holder (
  id UUID PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  cpf VARCHAR(11) NOT NULL UNIQUE,
  created_at TIMESTAMP DEFAULT now()
);

-- =======================
-- Account Table
-- =======================
CREATE TABLE account (
  id UUID PRIMARY KEY,
  holder_cpf VARCHAR(11) NOT NULL REFERENCES holder(cpf) ON DELETE CASCADE,
  number VARCHAR(20) NOT NULL UNIQUE,
  branch VARCHAR(10) NOT NULL DEFAULT '0001',
  balance NUMERIC(19,2) DEFAULT 0 NOT NULL,
  status VARCHAR(20) DEFAULT 'ACTIVE' NOT NULL,
  is_blocked BOOLEAN DEFAULT FALSE,
  last_daily_withdrawal_date DATE,
  total_daily_withdrawal NUMERIC(19,2) DEFAULT 0,
  created_at TIMESTAMP DEFAULT now(),
  updated_at TIMESTAMP DEFAULT now()
);

-- Index to improve account search by holder_cpf
CREATE INDEX idx_account_holder_cpf ON account(holder_cpf);

-- =======================
-- Transaction Table (Statement)
-- =======================
CREATE TABLE transaction (
  id UUID PRIMARY KEY,
  account_id UUID NOT NULL REFERENCES account(id) ON DELETE CASCADE,
  type VARCHAR(20) NOT NULL, -- DEPOSIT or WITHDRAWAL
  amount NUMERIC(19,2) NOT NULL CHECK (amount > 0),
  timestamp TIMESTAMP DEFAULT now(),
  description VARCHAR(255)
);

-- Performance indices
CREATE INDEX idx_transaction_account_id ON transaction(account_id);
CREATE INDEX idx_transaction_timestamp ON transaction(timestamp);

-- =======================
-- Triggers for automatic "updated_at" timestamp update
-- =======================
CREATE OR REPLACE FUNCTION update_timestamp()
RETURNS TRIGGER AS $$
BEGIN
  NEW.updated_at = now();
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_update_account_timestamp
BEFORE UPDATE ON account
FOR EACH ROW
EXECUTE FUNCTION update_timestamp();