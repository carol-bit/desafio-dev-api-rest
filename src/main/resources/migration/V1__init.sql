CREATE TABLE portador (
  id UUID PRIMARY KEY,
  nome VARCHAR(255) NOT NULL,
  cpf VARCHAR(11) NOT NULL UNIQUE,
  criado_em TIMESTAMP DEFAULT now()
);

CREATE TABLE conta (
  id UUID PRIMARY KEY,
  numero VARCHAR(20) NOT NULL,
  agencia VARCHAR(10) NOT NULL,
  portador_id UUID NOT NULL REFERENCES portador(id),
  saldo NUMERIC(19,2) DEFAULT 0 NOT NULL,
  ativa BOOLEAN DEFAULT TRUE,
  bloqueada BOOLEAN DEFAULT FALSE,
  ultimo_saque_dia DATE,
  total_saque_hoje NUMERIC(19,2) DEFAULT 0,
  criado_em TIMESTAMP DEFAULT now()
);

CREATE TABLE transacao (
  id UUID PRIMARY KEY,
  conta_id UUID NOT NULL REFERENCES conta(id),
  tipo VARCHAR(20) NOT NULL,
  valor NUMERIC(19,2) NOT NULL,
  data_hora TIMESTAMP DEFAULT now(),
  descricao VARCHAR(255)
);
