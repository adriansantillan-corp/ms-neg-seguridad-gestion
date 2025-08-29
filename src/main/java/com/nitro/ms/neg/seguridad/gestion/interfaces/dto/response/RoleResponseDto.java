package com.nitro.ms.neg.seguridad.gestion.interfaces.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class RoleResponseDto {
    private Long id;
    private String roleName;
    private String description;
    private String countryCode;
    private LocalDateTime createdAt;
}