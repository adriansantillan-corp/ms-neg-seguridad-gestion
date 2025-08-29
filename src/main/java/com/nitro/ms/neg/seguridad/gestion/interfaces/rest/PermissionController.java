package com.nitro.ms.neg.seguridad.gestion.interfaces.rest;

import com.nitro.ms.neg.seguridad.gestion.application.service.PermissionApplicationService;
import com.nitro.ms.neg.seguridad.gestion.domain.model.Permission;
import com.nitro.ms.neg.seguridad.gestion.interfaces.dto.request.AssignPermissionRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v1/permissions")
@RequiredArgsConstructor
@Tag(name = "Permission Management", description = "APIs for assigning permissions to roles")
public class PermissionController {

    private final PermissionApplicationService permissionApplicationService;

    @PostMapping("/assign")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Assign a permission (Action on a Resource) to a Role")
    public Mono<Permission> assignPermission(@Valid @RequestBody AssignPermissionRequestDto requestDto) {
        Permission permission = Permission.builder()
                .roleId(requestDto.getRoleId())
                .resourceId(requestDto.getResourceId())
                .actionId(requestDto.getActionId())
                .build();
        return permissionApplicationService.assignPermissionToRole(permission);
    }
}