package com.dock.digital.domain.service;

import com.dock.digital.domain.exceptions.BusinessRuleException;
import com.dock.digital.domain.exceptions.ResourceNotFoundException;
import com.dock.digital.domain.model.Account;
import com.dock.digital.domain.model.Holder;
import com.dock.digital.domain.model.Transaction;
import com.dock.digital.domain.model.Transaction.Type;
import com.dock.digital.domain.ports.output.AccountRepositoryPort;
import com.dock.digital.domain.ports.output.HolderRepositoryPort;
import com.dock.digital.domain.ports.output.TransactionRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Account Service Unit Tests")
class AccountServiceTest {

    @Mock
    private AccountRepositoryPort accountRepo;

    @Mock
    private HolderRepositoryPort holderRepo;

    @Mock
    private TransactionRepositoryPort transactionRepo;

    @InjectMocks
    private AccountService accountService;

    private UUID accountId;
    private String holderCpf;
    private Account activeAccount;

    @BeforeEach
    void setUp() {
        accountId = UUID.randomUUID();
        holderCpf = "12345678900";
        activeAccount = new Account(
                accountId,
                holderCpf,
                "00000001",
                "0001",
                BigDecimal.valueOf(1000.00),
                Account.Status.ACTIVE,
                false
        );
    }

    private void mockFindById(Account account) {
        when(accountRepo.findById(any(UUID.class))).thenReturn(Optional.of(account));
    }

    private void mockSaveAccount(Account account) {
        when(accountRepo.save(any(Account.class))).thenReturn(account);
    }

    private void mockFindHolder(Holder holder) {
        when(holderRepo.findByCpf(any(String.class))).thenReturn(Optional.of(holder));
    }

    @Test
    @DisplayName("Should create account successfully when holder exists")
    void createAccountWithHolderCpf_Success() {
        Holder holder = new Holder(UUID.randomUUID(), "Test Holder", holderCpf);
        mockFindHolder(holder);

        Account newlyCreatedAccountMock = new Account(
                accountId,
                holderCpf,
                "00000001",
                "0001",
                BigDecimal.ZERO,
                Account.Status.ACTIVE,
                false
        );

        mockSaveAccount(newlyCreatedAccountMock);

        Account result = accountService.createAccountWithHolderCpf(holderCpf);

        assertNotNull(result);
        assertEquals(holderCpf, result.holderCpf());
        assertEquals(Account.Status.ACTIVE, result.status());
        assertEquals(BigDecimal.ZERO, result.balance());

        verify(holderRepo, times(1)).findByCpf(holderCpf);
        verify(accountRepo, times(1)).save(any(Account.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when holder does not exist")
    void createAccountWithHolderCpf_HolderNotFound() {
        when(holderRepo.findByCpf(any(String.class))).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                accountService.createAccountWithHolderCpf("99999999999")
        );

        verify(holderRepo, times(1)).findByCpf(any(String.class));
        verify(accountRepo, never()).save(any());
    }

    @Test
    @DisplayName("Should perform a deposit successfully and save transaction")
    void deposit_Success() {
        BigDecimal depositAmount = BigDecimal.valueOf(500.00);
        BigDecimal expectedBalance = activeAccount.balance().add(depositAmount);

        mockFindById(activeAccount);
        mockSaveAccount(new Account(activeAccount.id(), holderCpf, activeAccount.accountNumber(), activeAccount.branch(), expectedBalance, activeAccount.status(), activeAccount.isBlocked()));

        Account result = accountService.deposit(accountId, depositAmount);

        assertEquals(expectedBalance, result.balance());

        verify(accountRepo, times(1)).save(any(Account.class));
        verify(transactionRepo, times(1)).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException for zero or negative deposit amount")
    void deposit_InvalidAmount() {
        assertThrows(IllegalArgumentException.class, () ->
                accountService.deposit(accountId, BigDecimal.valueOf(0))
        );
        assertThrows(IllegalArgumentException.class, () ->
                accountService.deposit(accountId, BigDecimal.valueOf(-10.0))
        );
        verify(accountRepo, never()).findById(any());
        verify(accountRepo, never()).save(any());
        verify(transactionRepo, never()).save(any());
    }

    @Test
    @DisplayName("Should throw BusinessRuleException if account is blocked or inactive")
    void deposit_AccountBlocked() {
        Account blockedAccount = new Account(activeAccount.id(), holderCpf, activeAccount.accountNumber(), activeAccount.branch(), activeAccount.balance(), Account.Status.ACTIVE, true);
        mockFindById(blockedAccount);

        assertThrows(BusinessRuleException.class, () ->
                accountService.deposit(accountId, BigDecimal.valueOf(100.0))
        );
        verify(accountRepo, never()).save(any());
    }

    @Test
    @DisplayName("Should perform a withdrawal successfully and save transaction")
    void withdraw_Success() {
        BigDecimal withdrawAmount = BigDecimal.valueOf(100.00);
        BigDecimal expectedBalance = activeAccount.balance().subtract(withdrawAmount);

        mockFindById(activeAccount);
        when(transactionRepo.findByPeriod(eq(accountId), any(OffsetDateTime.class), any(OffsetDateTime.class)))
                .thenReturn(Collections.emptyList());

        mockSaveAccount(new Account(activeAccount.id(), holderCpf, activeAccount.accountNumber(), activeAccount.branch(), expectedBalance, activeAccount.status(), activeAccount.isBlocked()));

        Account result = accountService.withdraw(accountId, withdrawAmount);

        assertEquals(expectedBalance, result.balance());
        verify(accountRepo, times(1)).save(any(Account.class));
        verify(transactionRepo, times(1)).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException if individual amount exceeds limit")
    void withdraw_IndividualLimitExceeded() {
        BigDecimal largeAmount = BigDecimal.valueOf(2000.01);

        assertThrows(IllegalArgumentException.class, () ->
                accountService.withdraw(accountId, largeAmount)
        );
        verify(accountRepo, never()).findById(any());
        verify(accountRepo, never()).save(any());
    }

    @Test
    @DisplayName("Should throw BusinessRuleException if daily total limit is exceeded")
    void withdraw_DailyLimitExceeded() {
        BigDecimal firstWithdrawal = BigDecimal.valueOf(1500.00);
        BigDecimal secondWithdrawal = BigDecimal.valueOf(600.00);

        Account accountWithFunds = new Account(accountId, holderCpf, "00000001", "0001", BigDecimal.valueOf(3000.00), Account.Status.ACTIVE, false);
        mockFindById(accountWithFunds);

        Transaction existingTransaction = Transaction.of(accountId, Type.WITHDRAWAL, firstWithdrawal);
        when(transactionRepo.findByPeriod(eq(accountId), any(OffsetDateTime.class), any(OffsetDateTime.class)))
                .thenReturn(List.of(existingTransaction));

        assertThrows(BusinessRuleException.class, () ->
                accountService.withdraw(accountId, secondWithdrawal)
        );
        verify(accountRepo, never()).save(any());
    }

    @Test
    @DisplayName("Should throw BusinessRuleException for insufficient balance")
    void withdraw_InsufficientBalance() {
        BigDecimal withdrawAmount = BigDecimal.valueOf(1000.01);
        mockFindById(activeAccount);

        when(transactionRepo.findByPeriod(eq(accountId), any(OffsetDateTime.class), any(OffsetDateTime.class)))
                .thenReturn(Collections.emptyList());

        assertThrows(BusinessRuleException.class, () ->
                accountService.withdraw(accountId, withdrawAmount)
        );
        verify(accountRepo, never()).save(any());
    }

    @Test
    @DisplayName("Should close account successfully when balance is zero")
    void closeAccount_Success() {
        Account zeroBalanceAccount = new Account(activeAccount.id(), holderCpf, activeAccount.accountNumber(), activeAccount.branch(), BigDecimal.ZERO, Account.Status.ACTIVE, false);
        mockFindById(zeroBalanceAccount);

        Account closedAccount = new Account(zeroBalanceAccount.id(), holderCpf, zeroBalanceAccount.accountNumber(), zeroBalanceAccount.branch(), BigDecimal.ZERO, Account.Status.CLOSED, false);
        when(accountRepo.save(any(Account.class))).thenReturn(closedAccount);

        Account result = accountService.closeAccount(accountId);

        assertEquals(Account.Status.CLOSED, result.status());
        verify(accountRepo, times(1)).save(any(Account.class));
    }

    @Test
    @DisplayName("Should throw BusinessRuleException if balance is not zero")
    void closeAccount_BalanceNotZero() {
        mockFindById(activeAccount);

        assertThrows(BusinessRuleException.class, () ->
                accountService.closeAccount(accountId)
        );

        verify(accountRepo, never()).save(any());
    }
}