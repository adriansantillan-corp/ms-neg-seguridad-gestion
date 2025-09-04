package com.nitro.ms.neg.seguridad.gestion.infrastructure.persistence.mapper;

import com.nitro.ms.neg.seguridad.gestion.domain.model.User;
import com.nitro.ms.neg.seguridad.gestion.infrastructure.persistence.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserPersistenceMapper {

    public User toDomain(UserEntity entity) {
        if (entity == null) {
            return null;
        }
        return User.builder()
                .id(entity.getId())
                .cognitoSub(entity.getCognitoSub())
                .userPrincipalName(entity.getUserPrincipalName())
                .username(entity.getUsername())
                .email(entity.getEmail())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .phone(entity.getPhone())
                .countryCode(entity.getCountryCode())
                .profileData(entity.getProfileData())
                .enabled(entity.isEnabled())
                .accountNonLocked(entity.isAccountNonLocked())
                //.credentialsNonExpired(entity.isCredentialsNonExpired())
                .accountNonExpired(entity.isAccountNonExpired())
                .lastLogin(entity.getLastLogin())
                .createdBy(entity.getCreatedBy())
                .createdAt(entity.getCreatedAt())
                .updatedBy(entity.getUpdatedBy())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public UserEntity toEntity(User domain) {
        if (domain == null) {
            return null;
        }
        UserEntity entity = new UserEntity();
        entity.setId(domain.getId());
        entity.setCognitoSub(domain.getCognitoSub());
        entity.setUserPrincipalName(domain.getUserPrincipalName());
        entity.setUsername(domain.getUsername());
        entity.setEmail(domain.getEmail());
        entity.setFirstName(domain.getFirstName());
        entity.setLastName(domain.getLastName());
        entity.setPhone(domain.getPhone());
        entity.setCountryCode(domain.getCountryCode());
        entity.setProfileData(domain.getProfileData());
        entity.setEnabled(domain.isEnabled());
        entity.setAccountNonLocked(domain.isAccountNonLocked());
        //entity.setCredentialsNonExpired(domain.isCredentialsNonExpired());
        entity.setAccountNonExpired(domain.isAccountNonExpired());
        entity.setLastLogin(domain.getLastLogin());
        entity.setCreatedBy(domain.getCreatedBy());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedBy(domain.getUpdatedBy());
        entity.setUpdatedAt(domain.getUpdatedAt());
        return entity;
    }
}