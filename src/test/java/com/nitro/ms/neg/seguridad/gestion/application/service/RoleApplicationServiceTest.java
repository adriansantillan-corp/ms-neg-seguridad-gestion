package com.nitro.ms.neg.seguridad.gestion.application.service;

import com.nitro.ms.neg.seguridad.gestion.domain.model.Role;
import com.nitro.ms.neg.seguridad.gestion.domain.repository.RoleRepository;
import com.nitro.ms.neg.seguridad.gestion.infrastructure.exception.specific_exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoleApplicationServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleApplicationService roleApplicationService;

    private Role role;

    @BeforeEach
    void setUp() {
        role = Role.builder().id(1L).roleName("VENDEDOR").countryCode("PE").build();
    }

    @Test
    @DisplayName("findRoleById should return role when found")
    void findRoleById_ShouldReturnRole_WhenFound() {
        when(roleRepository.findById(1L)).thenReturn(Mono.just(role));
        StepVerifier.create(roleApplicationService.findRoleById(1L))
                .expectNext(role)
                .verifyComplete();
    }

    @Test
    @DisplayName("findRoleById should return error when not found")
    void findRoleById_ShouldReturnError_WhenNotFound() {
        when(roleRepository.findById(anyLong())).thenReturn(Mono.empty());
        StepVerifier.create(roleApplicationService.findRoleById(99L))
                .expectError(ResourceNotFoundException.class)
                .verify();
    }

    @Test
    @DisplayName("createRole should save role when it does not exist")
    void createRole_ShouldSaveRole_WhenDoesNotExist() {
        when(roleRepository.existsByRoleNameAndCountryCode(role.getRoleName(), role.getCountryCode())).thenReturn(Mono.just(false));
        when(roleRepository.save(any(Role.class))).thenReturn(Mono.just(role));
        StepVerifier.create(roleApplicationService.createRole(role))
                .expectNext(role)
                .verifyComplete();
    }

    @Test
    @DisplayName("createRole should return error when role already exists")
    void createRole_ShouldReturnError_WhenRoleExists() {
        when(roleRepository.existsByRoleNameAndCountryCode(role.getRoleName(), role.getCountryCode())).thenReturn(Mono.just(true));
        StepVerifier.create(roleApplicationService.createRole(role))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    @DisplayName("findAllRoles should return all roles")
    void findAllRoles_ShouldReturnAllRoles() {
        Role role2 = Role.builder().id(2L).roleName("SUPERVISOR").build();
        when(roleRepository.findAll()).thenReturn(Flux.just(role, role2));
        StepVerifier.create(roleApplicationService.findAllRoles())
                .expectNext(role)
                .expectNext(role2)
                .verifyComplete();
    }
}