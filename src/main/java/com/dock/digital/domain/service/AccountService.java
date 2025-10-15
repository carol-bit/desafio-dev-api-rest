package com.dock.digital.domain.service;

import com.dock.digital.domain.exceptions.ResourceNotFoundException;
import com.dock.digital.domain.exceptions.BusinessRuleException;
import com.dock.digital.domain.model.Account;
import com.dock.digital.domain.model.Holder;
import com.dock.digital.domain.model.Transaction;
import com.dock.digital.domain.model.Transaction.Type;
import com.dock.digital.domain.ports.input.AccountServicePort;
import com.dock.digital.domain.ports.output.AccountRepositoryPort;
import com.dock.digital.domain.ports.output.HolderRepositoryPort;
import com.dock.digital.domain.ports.output.TransactionRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.*;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AccountService implements AccountServicePort {

    private static final BigDecimal DAILY_WITHDRAWAL_LIMIT = BigDecimal.valueOf(2000);
    private static final ZoneOffset DEFAULT_ZONE_OFFSET = ZoneOffset.UTC;

    private final AccountRepositoryPort accountRepo;
    private final HolderRepositoryPort holderRepo;
    private final TransactionRepositoryPort transactionRepo;

    public AccountService(AccountRepositoryPort accountRepo,
                          HolderRepositoryPort holderRepo,
                          TransactionRepositoryPort transactionRepo) {
        this.accountRepo = accountRepo;
        this.holderRepo = holderRepo;
        this.transactionRepo = transactionRepo;
    }

    @Override
    @Transactional
    public Account createAccountWithHolderCpf(String holderCpf) {
        Holder holder = holderRepo.findByCpf(holderCpf)
                .orElseThrow(() -> new ResourceNotFoundException("Holder not found"));

        Account newAccount = Account.openNew(holder.cpf());
        return accountRepo.save(newAccount);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Account> listAccounts() {
        return accountRepo.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Account findById(UUID id) {
        return accountRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Account not found"));
    }

    @Override
    @Transactional
    public Account blockAccount(UUID id) {
        Account account = findById(id);
        return saveAccount(account.id(), account.holderCpf(), account.accountNumber(), account.branch(), account.balance(), account.status(), true);
    }

    @Override
    @Transactional
    public Account unblockAccount(UUID id) {
        Account account = findById(id);
        return saveAccount(account.id(), account.holderCpf(), account.accountNumber(), account.branch(), account.balance(), account.status(), false);
    }

    @Override
    @Transactional
    public Account closeAccount(UUID id) {
        Account account = findById(id);
        if (account.balance().compareTo(BigDecimal.ZERO) != 0) {
            throw new BusinessRuleException("Account can only be closed if the balance is zero.");
        }
        return saveAccount(account.id(), account.holderCpf(), account.accountNumber(), account.branch(), account.balance(), Account.Status.CLOSED, account.isBlocked());
    }

    @Override
    @Transactional
    public Account deposit(UUID id, BigDecimal amount) {
        validateDepositAmount(amount);

        Account account = findById(id);
        validateStatus(account);

        BigDecimal newBalance = account.balance().add(amount);
        Account saved = saveAccount(account.id(), account.holderCpf(), account.accountNumber(), account.branch(), newBalance, account.status(), account.isBlocked());

        transactionRepo.save(Transaction.of(saved.id(), Type.DEPOSIT, amount));
        return saved;
    }

    @Override
    @Transactional
    public Account withdraw(UUID id, BigDecimal amount) {
        validateWithdrawalAmount(amount);

        Account account = findById(id);
        validateStatus(account);

        validateDailyLimit(account.id(), amount);

        if (account.balance().compareTo(amount) < 0) {
            throw new BusinessRuleException("Insufficient balance");
        }

        BigDecimal newBalance = account.balance().subtract(amount);
        Account saved = saveAccount(account.id(), account.holderCpf(), account.accountNumber(), account.branch(), newBalance, account.status(), account.isBlocked());

        transactionRepo.save(Transaction.of(saved.id(), Type.WITHDRAWAL, amount));
        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Transaction> getStatement(UUID accountId, OffsetDateTime start, OffsetDateTime end) {
        if (start == null || end == null || start.isAfter(end)) {
            throw new IllegalArgumentException("Invalid period");
        }
        findById(accountId);
        return transactionRepo.findByPeriod(accountId, start, end);
    }

    private Account saveAccount(UUID id, String holderCpf, String accountNumber, String branch, BigDecimal balance, Account.Status status, boolean isBlocked) {
        Account updated = new Account(id, holderCpf, accountNumber, branch, balance, status, isBlocked);
        return accountRepo.save(updated);
    }

    private void validateDepositAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Invalid amount for deposit");
        }
    }

    private void validateWithdrawalAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Invalid amount for withdrawal");
        }
        if (amount.compareTo(DAILY_WITHDRAWAL_LIMIT) > 0) {
            throw new IllegalArgumentException("Individual amount exceeds daily withdrawal limit (R$" + DAILY_WITHDRAWAL_LIMIT + ")");
        }
    }

    private void validateStatus(Account account) {
        if (account.status() != Account.Status.ACTIVE || account.isBlocked()) {
            throw new BusinessRuleException("Operation only allowed for active and unblocked accounts");
        }
    }

    private void validateDailyLimit(UUID accountId, BigDecimal amount) {

        OffsetDateTime startOfDay = LocalDate.now(DEFAULT_ZONE_OFFSET).atStartOfDay().atOffset(DEFAULT_ZONE_OFFSET);
        OffsetDateTime endOfDay = LocalDate.now(DEFAULT_ZONE_OFFSET).atTime(LocalTime.MAX).atOffset(DEFAULT_ZONE_OFFSET);

        List<Transaction> todayWithdrawals = transactionRepo.findByPeriod(accountId, startOfDay, endOfDay).stream()
                .filter(t -> t.type() == Type.WITHDRAWAL)
                .collect(Collectors.toList());

        BigDecimal totalWithdrawalsToday = todayWithdrawals.stream()
                .map(Transaction::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalWithdrawalsToday.add(amount).compareTo(DAILY_WITHDRAWAL_LIMIT) > 0) {
            throw new BusinessRuleException("Daily withdrawal limit (R$" + DAILY_WITHDRAWAL_LIMIT + ") exceeded");
        }
    }
}