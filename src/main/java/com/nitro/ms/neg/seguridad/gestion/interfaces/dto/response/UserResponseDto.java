package com.nitro.ms.neg.seguridad.gestion.interfaces.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class UserResponseDto {
    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private String countryCode;
    private boolean enabled;
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;
}
