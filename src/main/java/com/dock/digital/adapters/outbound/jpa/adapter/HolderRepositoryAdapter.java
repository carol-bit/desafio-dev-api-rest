package com.dock.digital.adapters.outbound.jpa.adapter;

import com.dock.digital.adapters.outbound.jpa.mappers.HolderMapper;
import com.dock.digital.adapters.outbound.jpa.persistence.HolderRepositoryJpa;
import com.dock.digital.adapters.outbound.jpa.entities.HolderEntity;
import com.dock.digital.domain.model.Holder;
import com.dock.digital.domain.ports.output.HolderRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class HolderRepositoryAdapter implements HolderRepositoryPort {

    private final HolderRepositoryJpa jpaRepository;
    private final HolderMapper mapper;

    @Override
    public Holder save(Holder holder) {
        HolderEntity entity = mapper.toEntity(holder);
        return mapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    public Optional<Holder> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Holder> findByCpf(String cpf) {
        return jpaRepository.findByCpf(cpf).map(mapper::toDomain);
    }

    @Override
    public List<Holder> findAll() {
        return jpaRepository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public void delete(UUID id) {
        jpaRepository.deleteById(id);
    }
}