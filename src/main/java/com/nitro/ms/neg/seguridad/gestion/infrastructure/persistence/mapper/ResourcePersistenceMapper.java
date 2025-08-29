package com.nitro.ms.neg.seguridad.gestion.infrastructure.persistence.mapper;

import com.nitro.ms.neg.seguridad.gestion.domain.model.Resource;
import com.nitro.ms.neg.seguridad.gestion.infrastructure.persistence.entity.ResourceEntity;
import org.springframework.stereotype.Component;

@Component
public class ResourcePersistenceMapper {

    public Resource toDomain(ResourceEntity entity) {
        if (entity == null) return null;
        return Resource.builder()
                .id(entity.getId())
                .resourceName(entity.getResourceName())
                .description(entity.getDescription())
                .moduleId(entity.getModule()!= null? entity.getModule().getId() : null)
                .createdBy(entity.getCreatedBy())
                .createdAt(entity.getCreatedAt())
                .updatedBy(entity.getUpdatedBy())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public ResourceEntity toEntity(Resource domain) {
        if (domain == null) return null;
        ResourceEntity entity = new ResourceEntity();
        entity.setId(domain.getId());
        entity.setResourceName(domain.getResourceName());
        entity.setDescription(domain.getDescription());
        // La entidad Module se establecerá en el adaptador antes de guardar
        return entity;
    }
}
