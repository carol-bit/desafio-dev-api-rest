package com.dock.digital.adapters.outbound.jpa.mappers;

import com.dock.digital.adapters.outbound.jpa.entities.HolderEntity;
import com.dock.digital.domain.model.Holder;
import org.springframework.stereotype.Component;

@Component
public class HolderMapper {

    public HolderEntity toEntity(Holder domain) {
        if (domain == null) {
            return null;
        }

        HolderEntity entity = new HolderEntity();

        entity.setId(domain.id());
        entity.setCpf(domain.cpf());
        entity.setName(domain.name());

        return entity;
    }

    public Holder toDomain(HolderEntity entity) {
        if (entity == null) {
            return null;
        }

        return new Holder(
                entity.getId(),
                entity.getCpf(),
                entity.getName()
        );
    }
}