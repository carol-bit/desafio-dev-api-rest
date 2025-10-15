package com.dock.digital.domain.ports.input;

import com.dock.digital.domain.model.Holder;
import java.util.List;
import java.util.UUID;


public interface HolderServicePort {

    Holder createHolder(Holder holder);

    List<Holder> listHolders();

    Holder findById(UUID id);

    void deleteHolder(UUID id);

    Holder findByCpf(String cpf);
}