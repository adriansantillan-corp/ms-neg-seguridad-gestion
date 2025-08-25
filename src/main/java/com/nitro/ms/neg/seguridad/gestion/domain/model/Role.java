package com.nitro.ms.neg.seguridad.gestion.domain.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class Role {
    private Long id;
    private String roleName;
    private String description;
    private String countryCode;
    private Long createdBy;
    private LocalDateTime createdAt;
    private Long updatedBy;
    private LocalDateTime updatedAt;
}
