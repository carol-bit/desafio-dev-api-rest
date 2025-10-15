package com.dock.digital.adapters.outbound.jpa.persistence;

import com.dock.digital.adapters.outbound.jpa.entities.HolderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface HolderRepositoryJpa extends JpaRepository<HolderEntity, UUID> {
    Optional<HolderEntity> findByCpf(String cpf);
}