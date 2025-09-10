package com.nitro.ms.neg.seguridad.gestion.interfaces.rest;

import com.nitro.ms.neg.seguridad.gestion.application.service.PermissionApplicationService;
import com.nitro.ms.neg.seguridad.gestion.domain.model.Permission;
import com.nitro.ms.neg.seguridad.gestion.interfaces.dto.request.AssignPermissionRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Set;

@RestController
@RequestMapping("/v1/permissions")
@RequiredArgsConstructor
@Tag(name = "Permission Management", description = "APIs for assigning permissions to roles")
public class PermissionController {

    private final PermissionApplicationService permissionApplicationService;
    private final Validator validator; // ← Inyecta el Validator

    @PostMapping("/assign")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Assign a permission (Action on a Resource) to a Role")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public Mono<Permission> assignPermission(@Valid @RequestBody AssignPermissionRequestDto requestDto) {
        // VALIDACIÓN MANUAL - así funciona con @WebFluxTest
        Set<ConstraintViolation<AssignPermissionRequestDto>> violations = validator.validate(requestDto);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException("Validation failed", violations);
        }
        Permission permission = Permission.builder()
                .roleId(requestDto.getRoleId())
                .resourceId(requestDto.getResourceId())
                .actionId(requestDto.getActionId())
                .build();
        return permissionApplicationService.assignPermissionToRole(permission);
    }
}