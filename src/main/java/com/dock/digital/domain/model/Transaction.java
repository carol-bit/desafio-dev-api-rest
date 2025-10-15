package com.dock.digital.domain.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record Transaction(
        UUID id,
        UUID accountId,
        Type type,
        BigDecimal amount,
        OffsetDateTime timestamp
) {
    public enum Type {
        DEPOSIT,
        WITHDRAWAL
    }

    public Transaction {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transaction amount must be positive.");
        }
        if (timestamp == null) {
            throw new IllegalArgumentException("timestamp cannot be null.");
        }
    }

    public static Transaction of(UUID accountId, Type type, BigDecimal amount) {
        return new Transaction(UUID.randomUUID(), accountId, type, amount, OffsetDateTime.now());
    }
}