package com.nitro.ms.neg.seguridad.gestion.infrastructure.persistence.mapper;

import com.nitro.ms.neg.seguridad.gestion.domain.model.Action;
import com.nitro.ms.neg.seguridad.gestion.infrastructure.persistence.entity.ActionEntity;
import org.springframework.stereotype.Component;

@Component
public class ActionPersistenceMapper {

    public Action toDomain(ActionEntity entity) {
        if (entity == null) return null;
        return Action.builder()
                .id(entity.getId())
                .actionName(entity.getActionName())
                .description(entity.getDescription())
                .createdBy(entity.getCreatedBy())
                .createdAt(entity.getCreatedAt())
                .updatedBy(entity.getUpdatedBy())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public ActionEntity toEntity(Action domain) {
        if (domain == null) return null;
        ActionEntity entity = new ActionEntity();
        entity.setId(domain.getId());
        entity.setActionName(domain.getActionName());
        entity.setDescription(domain.getDescription());
        return entity;
    }
}