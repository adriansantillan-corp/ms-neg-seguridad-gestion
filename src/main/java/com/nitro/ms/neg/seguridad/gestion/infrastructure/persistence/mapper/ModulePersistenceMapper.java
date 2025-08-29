package com.nitro.ms.neg.seguridad.gestion.infrastructure.persistence.mapper;

import com.nitro.ms.neg.seguridad.gestion.domain.model.Module;
import com.nitro.ms.neg.seguridad.gestion.infrastructure.persistence.entity.ModuleEntity;
import org.springframework.stereotype.Component;

@Component
public class ModulePersistenceMapper {

    public Module toDomain(ModuleEntity entity) {
        if (entity == null) return null;
        return Module.builder()
                .id(entity.getId())
                .moduleName(entity.getModuleName())
                .description(entity.getDescription())
                .createdBy(entity.getCreatedBy())
                .createdAt(entity.getCreatedAt())
                .updatedBy(entity.getUpdatedBy())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public ModuleEntity toEntity(Module domain) {
        if (domain == null) return null;
        ModuleEntity entity = new ModuleEntity();
        entity.setId(domain.getId());
        entity.setModuleName(domain.getModuleName());
        entity.setDescription(domain.getDescription());
        return entity;
    }
}