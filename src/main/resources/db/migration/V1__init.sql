CREATE TABLE holder (
  id UUID PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  cpf VARCHAR(11) NOT NULL UNIQUE,
  created_at TIMESTAMP DEFAULT now()
);

---

CREATE TABLE account (
  id UUID PRIMARY KEY,
  number VARCHAR(20) NOT NULL,
  branch VARCHAR(10) NOT NULL, -- Agência
  holder_cpf VARCHAR(11) NOT NULL, -- CPF do Titular
  balance NUMERIC(19,2) DEFAULT 0 NOT NULL, -- Saldo

  status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE', -- Status

  is_blocked BOOLEAN DEFAULT FALSE, -- Bloqueada
  last_daily_withdrawal_date DATE, -- Último saque do dia
  total_daily_withdrawal NUMERIC(19,2) DEFAULT 0, -- Total saque hoje
  created_at TIMESTAMP DEFAULT now()
);

---

CREATE TABLE transaction (
  id UUID PRIMARY KEY,
  account_id UUID NOT NULL REFERENCES account(id),
  type VARCHAR(20) NOT NULL,
  amount NUMERIC(19,2) NOT NULL, -- Valor
  timestamp TIMESTAMP DEFAULT now(), -- Data/Hora
  description VARCHAR(255)
);