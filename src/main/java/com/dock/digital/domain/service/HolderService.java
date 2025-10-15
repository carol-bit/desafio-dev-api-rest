package com.dock.digital.domain.service;

import com.dock.digital.domain.exceptions.ResourceNotFoundException;
import com.dock.digital.domain.model.Holder;
import com.dock.digital.domain.ports.input.HolderServicePort;
import com.dock.digital.domain.ports.output.HolderRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class HolderService implements HolderServicePort {

    private final HolderRepositoryPort repositoryPort;

    public HolderService(HolderRepositoryPort repositoryPort) {
        this.repositoryPort = repositoryPort;
    }

    @Override
    public Holder createHolder(Holder holder) {
        return repositoryPort.save(holder);
    }

    @Override
    public List<Holder> listHolders() {
        return repositoryPort.findAll();
    }

    @Override
    public Holder findById(UUID id) {
        return findHolderById(id);
    }

    @Override
    public void deleteHolder(UUID id) {
        repositoryPort.delete(id);
    }

    @Override
    public Holder findByCpf(String cpf) {
        return findHolderByCpf(cpf);
    }

    private Holder findHolderById(UUID id) {
        return repositoryPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Holder not found by ID."));
    }

    private Holder findHolderByCpf(String cpf) {
        return repositoryPort.findByCpf(cpf)
                .orElseThrow(() -> new ResourceNotFoundException("Holder not found by CPF."));
    }
}