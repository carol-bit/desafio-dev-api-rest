package com.dock.digital.domain.model;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.UUID;

public record Account(
        UUID id,
        String holderCpf,

        String accountNumber,
        String branch,
        BigDecimal balance,
        Status status,
        boolean isBlocked
) {
    public enum Status {
        ACTIVE, CLOSED
    }

    private static final SecureRandom random = new SecureRandom();

    public static Account openNew(String holderCpf) {
        return new Account(
                UUID.randomUUID(),
                holderCpf,
                generateAccountNumber(),
                "0001",
                BigDecimal.ZERO,
                Status.ACTIVE,
                false
        );
    }

    private static String generateAccountNumber() {
        int number = random.nextInt(100_000_000);
        return String.format("%08d", number);
    }

    public Account {
        if (balance == null || balance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Account balance cannot be negative.");
        }
    }
}