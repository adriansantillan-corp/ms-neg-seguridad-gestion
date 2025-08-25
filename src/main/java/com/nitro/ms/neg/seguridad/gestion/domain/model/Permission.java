package com.nitro.ms.neg.seguridad.gestion.domain.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class Permission {
    // La clave primaria es compuesta, por lo que no tenemos un ID simple.
    private Long roleId;
    private Long resourceId;
    private Long actionId;
    private LocalDateTime grantedAt;
    private Long grantedBy;
}
