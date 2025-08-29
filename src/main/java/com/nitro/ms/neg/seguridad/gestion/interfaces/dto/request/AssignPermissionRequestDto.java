package com.nitro.ms.neg.seguridad.gestion.interfaces.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AssignPermissionRequestDto {

    @NotNull(message = "Role ID cannot be null.")
    private Long roleId;

    @NotNull(message = "Resource ID cannot be null.")
    private Long resourceId;

    @NotNull(message = "Action ID cannot be null.")
    private Long actionId;
}