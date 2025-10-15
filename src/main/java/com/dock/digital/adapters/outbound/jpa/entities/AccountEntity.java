package com.dock.digital.adapters.outbound.jpa.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "account")
public class AccountEntity {

    public enum AccountStatus {
        ACTIVE, CLOSED
    }

    @Id
    private UUID id;

    @Column(name = "holder_cpf", nullable = false)
    private String holderCpf;

    @Column(name = "number")
    private String accountNumber;

    @Column(name = "branch")
    private String branch;

    @Column(name = "balance")
    private BigDecimal balance;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private AccountStatus status;

    @Column(name = "is_blocked")
    private Boolean isBlocked;

    public AccountEntity() {}

    public AccountEntity(UUID id, String holderCpf, String accountNumber, String branch,
                         BigDecimal balance, AccountStatus status, Boolean isBlocked) {
        this.id = id;
        this.holderCpf = holderCpf;
        this.accountNumber = accountNumber;
        this.branch = branch;
        this.balance = balance;
        this.status = status;
        this.isBlocked = isBlocked;
    }

    public UUID getId() { return id; }
    public String getHolderCpf() { return holderCpf; }
    public String getAccountNumber() { return accountNumber; }
    public String getBranch() { return branch; }
    public BigDecimal getBalance() { return balance; }
    public AccountStatus getStatus() { return status; }
    public Boolean getIsBlocked() { return isBlocked; }

    public void setId(UUID id) { this.id = id; }
    public void setHolderCpf(String holderCpf) { this.holderCpf = holderCpf; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }
    public void setBranch(String branch) { this.branch = branch; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }
    public void setStatus(AccountStatus status) { this.status = status; }
    public void setIsBlocked(Boolean isBlocked) { this.isBlocked = isBlocked; }
}