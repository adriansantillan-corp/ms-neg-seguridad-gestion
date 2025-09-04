package com.nitro.ms.neg.seguridad.gestion.domain.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class User {
    private Long id;
    private String cognitoSub;
    private String userPrincipalName;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private String countryCode;
    private String profileData; // JSONB como String en el dominio, se manejará en la capa de persistencia
    private boolean enabled;
    private boolean accountNonLocked;
    //private boolean credentialsNonExpired;
    private boolean accountNonExpired;
    private LocalDateTime lastLogin;
    private Long createdBy;
    private LocalDateTime createdAt;
    private Long updatedBy;
    private LocalDateTime updatedAt;
}
