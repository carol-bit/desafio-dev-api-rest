package com.dock.digital.adapters.outbound.jpa.persistence;

import com.dock.digital.adapters.outbound.jpa.entities.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AccountRepositoryJpa extends JpaRepository<AccountEntity, UUID> {
    Optional<AccountEntity> findByAccountNumber(String accountNumber);
}