package com.dock.digital.adapters.outbound.jpa.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "transaction")
public class TransactionEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "account_id", nullable = false)
    private UUID accountId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private Type type;

    @Column(name = "amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(name = "timestamp", nullable = false)
    private OffsetDateTime timestamp;

    public enum Type { DEPOSIT, WITHDRAWAL }

    public TransactionEntity() {}

    public TransactionEntity(UUID accountId, Type type, BigDecimal amount, OffsetDateTime timestamp) {
        this.accountId = accountId;
        this.type = type;
        this.amount = amount;
        this.timestamp = timestamp;
    }

    public UUID getId() { return id; }
    public UUID getAccountId() { return accountId; }
    public Type getType() { return type; }
    public BigDecimal getAmount() { return amount; }
    public OffsetDateTime getTimestamp() { return timestamp; }

    public void setId(UUID id) { this.id = id; }
    public void setAccountId(UUID accountId) { this.accountId = accountId; }
    public void setType(Type type) { this.type = type; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public void setTimestamp(OffsetDateTime timestamp) { this.timestamp = timestamp; }
}