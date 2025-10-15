package com.dock.digital.adapters.outbound.jpa.adapter;

import com.dock.digital.adapters.outbound.jpa.mappers.AccountMapper;
import com.dock.digital.adapters.outbound.jpa.persistence.AccountRepositoryJpa;
import com.dock.digital.adapters.outbound.jpa.entities.AccountEntity;
import com.dock.digital.domain.model.Account;
import com.dock.digital.domain.ports.output.AccountRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class AccountRepositoryAdapter implements AccountRepositoryPort {

    private final AccountRepositoryJpa jpa;

    public AccountRepositoryAdapter(AccountRepositoryJpa jpa) {
        this.jpa = jpa;
    }

    @Override
    public Account save(Account account) {
        AccountEntity entity = AccountMapper.toEntity(account);
        AccountEntity saved = jpa.save(entity);
        return AccountMapper.toDomain(saved);
    }

    @Override
    public Optional<Account> findById(UUID id) {
        return jpa.findById(id).map(AccountMapper::toDomain);
    }

    @Override
    public List<Account> findAll() {
        return jpa.findAll().stream().map(AccountMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public Optional<Account> findByAccountNumber(String number) {
        return jpa.findByAccountNumber(number).map(AccountMapper::toDomain);
    }
}