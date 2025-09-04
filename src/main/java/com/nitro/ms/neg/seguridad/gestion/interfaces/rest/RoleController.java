package com.nitro.ms.neg.seguridad.gestion.interfaces.rest;

import com.nitro.ms.neg.seguridad.gestion.application.service.RoleApplicationService;
import com.nitro.ms.neg.seguridad.gestion.domain.model.Role;
import com.nitro.ms.neg.seguridad.gestion.interfaces.dto.response.RoleResponseDto;
import com.nitro.ms.neg.seguridad.gestion.interfaces.mapper.RoleApiMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v1/roles")
@RequiredArgsConstructor
@Tag(name = "Role Management", description = "APIs for managing user roles")
@Validated // Habilita la validación para @PathVariable
public class RoleController {

    private final RoleApplicationService roleApplicationService;
    private final RoleApiMapper roleApiMapper;

    @GetMapping
    @Operation(summary = "Get all roles")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'SUPERVISOR')")
    public Flux<RoleResponseDto> getAllRoles() {
        return roleApplicationService.findAllRoles().map(roleApiMapper::toDto);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a role by its ID")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'SUPERVISOR')")
    public Mono<RoleResponseDto> getRoleById(
            @PathVariable @Min(value = 1, message = "Role ID must be a positive number.") Long id) {
        return roleApplicationService.findRoleById(id).map(roleApiMapper::toDto);
    }
}
