package com.nitro.ms.neg.seguridad.gestion.application.service;

import com.nitro.ms.neg.seguridad.gestion.domain.model.Action;
import com.nitro.ms.neg.seguridad.gestion.domain.model.Permission;
import com.nitro.ms.neg.seguridad.gestion.domain.model.Resource;
import com.nitro.ms.neg.seguridad.gestion.domain.model.Role;
import com.nitro.ms.neg.seguridad.gestion.domain.repository.ActionRepository;
import com.nitro.ms.neg.seguridad.gestion.domain.repository.PermissionRepository;
import com.nitro.ms.neg.seguridad.gestion.domain.repository.ResourceRepository;
import com.nitro.ms.neg.seguridad.gestion.domain.repository.RoleRepository;
import com.nitro.ms.neg.seguridad.gestion.infrastructure.exception.specific_exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class PermissionApplicationService {

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final ResourceRepository resourceRepository;
    private final ActionRepository actionRepository;

    public Mono<Permission> assignPermissionToRole(Permission permission) {
        Mono<Boolean> permissionExists = permissionRepository.exists(
                permission.getRoleId(),
                permission.getResourceId(),
                permission.getActionId()
        );

        Mono<Role> roleMono = roleRepository.findById(permission.getRoleId())
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Role not found with id: " + permission.getRoleId())));

        Mono<Resource> resourceMono = resourceRepository.findById(permission.getResourceId())
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Resource not found with id: " + permission.getResourceId())));

        Mono<Action> actionMono = actionRepository.findById(permission.getActionId())
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Action not found with id: " + permission.getActionId())));

        return permissionExists.flatMap(exists -> {
            if (exists) {
                return Mono.error(new IllegalArgumentException("This permission is already assigned to the role."));
            }
            return Mono.zip(roleMono, resourceMono, actionMono)
                    .flatMap(tuple -> {
                        Role foundRole = tuple.getT1();
                        Resource foundResource = tuple.getT2();
                        Action foundAction = tuple.getT3();
                        // Llamamos al método save con la firma correcta
                        return permissionRepository.save(permission, foundRole, foundResource, foundAction);
                    });
        });
    }

    public Flux<Permission> findPermissionsForRole(Long roleId) {
        return permissionRepository.findByRoleId(roleId);
    }
}