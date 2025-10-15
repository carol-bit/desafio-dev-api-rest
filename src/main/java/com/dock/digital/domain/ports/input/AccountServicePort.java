package com.dock.digital.domain.ports.input;

import com.dock.digital.domain.model.Account;
import com.dock.digital.domain.model.Transaction;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface AccountServicePort {

    Account createAccountWithHolderCpf(String holderCpf);

    List<Account> listAccounts();

    Account findById(UUID id);

    Account blockAccount(UUID id);

    Account unblockAccount(UUID id);

    Account closeAccount(UUID id);

    Account deposit(UUID id, BigDecimal amount);

    Account withdraw(UUID id, BigDecimal amount);

    List<Transaction> getStatement(UUID accountId, OffsetDateTime start, OffsetDateTime end);
}