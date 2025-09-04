package com.nitro.ms.neg.seguridad.gestion.interfaces.mapper;

import com.nitro.ms.neg.seguridad.gestion.domain.model.User;
import com.nitro.ms.neg.seguridad.gestion.interfaces.dto.request.CreateUserRequestDto;
import com.nitro.ms.neg.seguridad.gestion.interfaces.dto.response.UserResponseDto;
import org.springframework.stereotype.Component;

@Component
public class UserApiMapper {

    public User toDomain(CreateUserRequestDto dto) {
        return User.builder()
                .cognitoSub(dto.getCognitoSub())
                .username(dto.getUsername())
                .email(dto.getEmail())
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .phone(dto.getPhone())
                .countryCode(dto.getCountryCode())
                .enabled(true) // Por defecto al crear
                .accountNonLocked(true)
                .accountNonExpired(true)
                //.credentialsNonExpired(true)
                .build();
    }

    public UserResponseDto toDto(User domain) {
        return UserResponseDto.builder()
                .id(domain.getId())
                .username(domain.getUsername())
                .email(domain.getEmail())
                .firstName(domain.getFirstName())
                .lastName(domain.getLastName())
                .phone(domain.getPhone())
                .countryCode(domain.getCountryCode())
                .enabled(domain.isEnabled())
                .createdAt(domain.getCreatedAt())
                .lastLogin(domain.getLastLogin())
                .build();
    }
}
