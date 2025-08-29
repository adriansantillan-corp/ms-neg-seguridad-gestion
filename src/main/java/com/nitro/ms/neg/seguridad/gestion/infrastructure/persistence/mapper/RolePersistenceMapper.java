package com.nitro.ms.neg.seguridad.gestion.infrastructure.persistence.mapper;

import com.nitro.ms.neg.seguridad.gestion.domain.model.Role;
import com.nitro.ms.neg.seguridad.gestion.infrastructure.persistence.entity.RoleEntity;
import org.springframework.stereotype.Component;

@Component
public class RolePersistenceMapper {

    public Role toDomain(RoleEntity entity) {
        if (entity == null) {
            return null;
        }
        return Role.builder()
                .id(entity.getId())
                .roleName(entity.getRoleName())
                .description(entity.getDescription())
                .countryCode(entity.getCountryCode())
                .createdBy(entity.getCreatedBy())
                .createdAt(entity.getCreatedAt())
                .updatedBy(entity.getUpdatedBy())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public RoleEntity toEntity(Role domain) {
        if (domain == null) {
            return null;
        }
        RoleEntity entity = new RoleEntity();
        entity.setId(domain.getId());
        entity.setRoleName(domain.getRoleName());
        entity.setDescription(domain.getDescription());
        entity.setCountryCode(domain.getCountryCode());
        entity.setCreatedBy(domain.getCreatedBy());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedBy(domain.getUpdatedBy());
        entity.setUpdatedAt(domain.getUpdatedAt());
        return entity;
    }
}