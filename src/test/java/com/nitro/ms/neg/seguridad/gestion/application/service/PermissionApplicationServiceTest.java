// El código completo que te proporcioné en la respuesta anterior va aquí.
// Es la versión final y correcta.
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PermissionApplicationServiceTest {

    @Mock private PermissionRepository permissionRepository;
    @Mock private RoleRepository roleRepository;
    @Mock private ResourceRepository resourceRepository;
    @Mock private ActionRepository actionRepository;

    @InjectMocks
    private PermissionApplicationService permissionApplicationService;

    private Permission permission;
    private Role role;
    private Resource resource;
    private Action action;

    @BeforeEach
    void setUp() {
        permission = Permission.builder().roleId(1L).resourceId(1L).actionId(1L).build();
        role = Role.builder().id(1L).build();
        resource = Resource.builder().id(1L).build();
        action = Action.builder().id(1L).build();
    }

    @Test
    @DisplayName("assignPermissionToRole should save permission when all entities exist and permission is new")
    void assignPermission_ShouldSucceed_WhenValid() {
        when(permissionRepository.exists(1L, 1L, 1L)).thenReturn(Mono.just(false));
        when(roleRepository.findById(1L)).thenReturn(Mono.just(role));
        when(resourceRepository.findById(1L)).thenReturn(Mono.just(resource));
        when(actionRepository.findById(1L)).thenReturn(Mono.just(action));
        when(permissionRepository.save(any(), any(), any(), any())).thenReturn(Mono.just(permission));

        StepVerifier.create(permissionApplicationService.assignPermissionToRole(permission))
                .expectNext(permission)
                .verifyComplete();
    }

    @Test
    @DisplayName("assignPermissionToRole should return error when role does not exist")
    void assignPermission_ShouldFail_WhenRoleNotFound() {
        when(permissionRepository.exists(1L, 1L, 1L)).thenReturn(Mono.just(false));
        when(roleRepository.findById(1L)).thenReturn(Mono.empty());
        when(resourceRepository.findById(1L)).thenReturn(Mono.just(resource));
        when(actionRepository.findById(1L)).thenReturn(Mono.just(action));

        StepVerifier.create(permissionApplicationService.assignPermissionToRole(permission))
                .expectError(ResourceNotFoundException.class)
                .verify();
    }
}