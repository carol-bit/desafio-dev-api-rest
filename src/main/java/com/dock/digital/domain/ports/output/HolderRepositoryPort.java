package com.dock.digital.domain.ports.output;

import com.dock.digital.domain.model.Holder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface HolderRepositoryPort {

    Holder save(Holder holder);

    Optional<Holder> findById(UUID id);

    Optional<Holder> findByCpf(String cpf);

    List<Holder> findAll();

    void delete(UUID id);
}