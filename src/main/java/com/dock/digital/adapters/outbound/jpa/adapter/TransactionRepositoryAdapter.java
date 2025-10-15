package com.dock.digital.adapters.outbound.jpa.adapter;

import com.dock.digital.adapters.outbound.jpa.persistence.TransactionRepositoryJpa;
import com.dock.digital.adapters.outbound.jpa.entities.TransactionEntity;
import com.dock.digital.domain.model.Transaction;
import com.dock.digital.domain.ports.output.TransactionRepositoryPort;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class TransactionRepositoryAdapter implements TransactionRepositoryPort {

    private final TransactionRepositoryJpa jpa;

    public TransactionRepositoryAdapter(TransactionRepositoryJpa jpa) {
        this.jpa = jpa;
    }

    private TransactionEntity toEntity(Transaction transaction) {
        return new TransactionEntity(
                transaction.accountId(),
                TransactionEntity.Type.valueOf(transaction.type().name()),
                transaction.amount(),
                transaction.timestamp()
        );
    }

    private Transaction toDomain(TransactionEntity entity) {
        return new Transaction(
                entity.getId(),
                entity.getAccountId(),
                Transaction.Type.valueOf(entity.getType().name()),
                entity.getAmount(),
                entity.getTimestamp()
        );
    }

    @Override
    public void save(Transaction transaction) {
        jpa.save(toEntity(transaction));
    }

    @Override
    public List<Transaction> findByPeriod(UUID accountId, OffsetDateTime start, OffsetDateTime end) {
        return jpa.findByPeriod(accountId, start, end).stream().map(this::toDomain).collect(Collectors.toList());
    }

    public List<Transaction> findWithdrawalsByPeriod(UUID accountId, OffsetDateTime start, OffsetDateTime end) {
        return jpa.findByAccountAndTypeAndPeriod(accountId, TransactionEntity.Type.WITHDRAWAL, start, end)
                .stream().map(this::toDomain).collect(Collectors.toList());
    }
}