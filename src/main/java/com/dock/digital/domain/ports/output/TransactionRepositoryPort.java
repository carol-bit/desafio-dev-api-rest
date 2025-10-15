package com.dock.digital.domain.ports.output;

import com.dock.digital.domain.model.Transaction;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface TransactionRepositoryPort {
    void save(Transaction transaction);
    List<Transaction> findByPeriod(UUID accountId, OffsetDateTime start, OffsetDateTime end);
}