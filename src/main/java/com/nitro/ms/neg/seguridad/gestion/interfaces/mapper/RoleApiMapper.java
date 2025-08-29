package com.nitro.ms.neg.seguridad.gestion.interfaces.mapper;

import com.nitro.ms.neg.seguridad.gestion.domain.model.Role;
import com.nitro.ms.neg.seguridad.gestion.interfaces.dto.response.RoleResponseDto;
import org.springframework.stereotype.Component;

@Component
public class RoleApiMapper {

    public RoleResponseDto toDto(Role domain) {
        return RoleResponseDto.builder()
                .id(domain.getId())
                .roleName(domain.getRoleName())
                .description(domain.getDescription())
                .countryCode(domain.getCountryCode())
                .createdAt(domain.getCreatedAt())
                .build();
    }
}