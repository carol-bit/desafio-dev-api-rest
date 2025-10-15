package com.dock.digital.domain.ports.output;

import com.dock.digital.domain.model.Account;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccountRepositoryPort {
    Account save(Account account);
    Optional<Account> findById(UUID id);
    List<Account> findAll();
    Optional<Account> findByAccountNumber(String number);
}