package com.nitro.ms.neg.seguridad.gestion.infrastructure.persistence.mapper;

import com.nitro.ms.neg.seguridad.gestion.domain.model.Permission;
import com.nitro.ms.neg.seguridad.gestion.infrastructure.persistence.entity.*;
import org.springframework.stereotype.Component;

@Component
public class PermissionPersistenceMapper {

    public Permission toDomain(PermissionEntity entity) {
        if (entity == null) {
            return null;
        }
        return Permission.builder()
                .roleId(entity.getId().getRoleId())
                .resourceId(entity.getId().getResourceId())
                .actionId(entity.getId().getActionId())
                .grantedAt(entity.getGrantedAt())
                .grantedBy(entity.getGrantedBy())
                .build();
    }

    // MÉTODO ACTUALIZADO: Ahora recibe las entidades para asociarlas
    public PermissionEntity toEntity(Permission domain, RoleEntity role, ResourceEntity resource, ActionEntity action) {
        if (domain == null) {
            return null;
        }
        PermissionEntityId id = new PermissionEntityId(
                domain.getRoleId(),
                domain.getResourceId(),
                domain.getActionId()
        );
        PermissionEntity entity = new PermissionEntity();
        entity.setId(id);
        entity.setGrantedBy(domain.getGrantedBy());

        // ASOCIACIÓN CLAVE PARA JPA
        entity.setRole(role);
        entity.setResource(resource);
        entity.setAction(action);

        return entity;
    }
}