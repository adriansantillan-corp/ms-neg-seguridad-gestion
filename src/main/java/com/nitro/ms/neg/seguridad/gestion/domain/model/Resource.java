package com.nitro.ms.neg.seguridad.gestion.domain.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class Resource {
    private Long id;
    private String resourceName;
    private String description;
    private Long moduleId; // Relación con Module
    private Long createdBy;
    private LocalDateTime createdAt;
    private Long updatedBy;
    private LocalDateTime updatedAt;
}
