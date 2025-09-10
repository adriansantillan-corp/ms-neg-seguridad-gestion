package com.nitro.ms.neg.seguridad.gestion.interfaces.rest;

import com.nitro.ms.neg.seguridad.gestion.application.service.PermissionApplicationService;
import com.nitro.ms.neg.seguridad.gestion.domain.model.Permission;
import com.nitro.ms.neg.seguridad.gestion.infrastructure.config.TestSecurityConfig;
import com.nitro.ms.neg.seguridad.gestion.infrastructure.exception.GlobalExceptionHandler;
import com.nitro.ms.neg.seguridad.gestion.interfaces.dto.request.AssignPermissionRequestDto;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import java.util.HashSet;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@WebFluxTest(PermissionController.class)
@Import({TestSecurityConfig.class, PermissionControllerTest.TestConfig.class,
                        ValidationAutoConfiguration.class, GlobalExceptionHandler.class})  // ← Centraliza toda la seguridad aquí
@ActiveProfiles("test")
class PermissionControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private PermissionApplicationService permissionApplicationService;

    @MockitoBean // ← Mockea el Validator que se inyectará en el controlador
    private Validator validator;

    @TestConfiguration
    static class TestConfig {

        @Bean
        @Primary
        PermissionApplicationService permissionApplicationService() {
            return mock(PermissionApplicationService.class);
        }

    }

    @BeforeEach
    void setUp() {
        reset(permissionApplicationService);

        // Configura el mock del Validator para que no devuelva violaciones por defecto
        when(validator.validate(any())).thenReturn(new HashSet<>());
    }


    @Test
    @DisplayName("POST /v1/permissions/assign should return 401 Unauthorized without authentication")
    void assignPermission_ShouldReturn401_WhenNotAuthenticated() {
        AssignPermissionRequestDto requestDto = new AssignPermissionRequestDto();
        requestDto.setRoleId(1L);
        requestDto.setResourceId(1L);
        requestDto.setActionId(1L);

        webTestClient.post().uri("/v1/permissions/assign")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestDto)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    @WithMockUser(roles = {"ADMINISTRADOR"})  // ← ¡CAMBIADO A ROLES!
    @DisplayName("POST /v1/permissions/assign should return 201 Created when authenticated as ADMINISTRADOR")
    void assignPermission_ShouldReturn201_WhenAuthenticatedAsAdmin() {
        AssignPermissionRequestDto requestDto = new AssignPermissionRequestDto();
        requestDto.setRoleId(1L);
        requestDto.setResourceId(1L);
        requestDto.setActionId(1L);

        Permission expectedPermission = Permission.builder()
                .roleId(1L)
                .resourceId(1L)
                .actionId(1L)
                .build();

        when(permissionApplicationService.assignPermissionToRole(any(Permission.class)))
                .thenReturn(Mono.just(expectedPermission));

        webTestClient
                .post().uri("/v1/permissions/assign")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestDto)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Permission.class)
                .isEqualTo(expectedPermission);

        verify(permissionApplicationService).assignPermissionToRole(argThat(permission ->
                permission.getRoleId().equals(1L) &&
                        permission.getResourceId().equals(1L) &&
                        permission.getActionId().equals(1L)
        ));
    }

    @Test
    @WithMockUser(roles = {"SUPERVISOR"})
    @DisplayName("POST /v1/permissions/assign should return 403 Forbidden when not ADMINISTRADOR")
    void assignPermission_ShouldReturn403_WhenNotAdmin() {
        AssignPermissionRequestDto requestDto = new AssignPermissionRequestDto();
        requestDto.setRoleId(1L);
        requestDto.setResourceId(1L);
        requestDto.setActionId(1L);

        webTestClient
                .post().uri("/v1/permissions/assign")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestDto)
                .exchange()
                .expectStatus().isForbidden();

        verifyNoInteractions(permissionApplicationService);
    }

    @Test
    @WithMockUser(roles = {"VENDEDOR"})
    @DisplayName("POST /v1/permissions/assign should return 403 Forbidden with VENDEDOR role")
    void assignPermission_ShouldReturn403_WhenVendedor() {
        AssignPermissionRequestDto requestDto = new AssignPermissionRequestDto();
        requestDto.setRoleId(1L);
        requestDto.setResourceId(1L);
        requestDto.setActionId(1L);

        webTestClient
                .post().uri("/v1/permissions/assign")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestDto)
                .exchange()
                .expectStatus().isForbidden();

        verifyNoInteractions(permissionApplicationService);
    }

    @Test
    @WithMockUser  // Sin roles
    @DisplayName("POST /v1/permissions/assign should return 403 Forbidden without required role")
    void assignPermission_ShouldReturn403_WhenNoRequiredRole() {
        AssignPermissionRequestDto requestDto = new AssignPermissionRequestDto();
        requestDto.setRoleId(1L);
        requestDto.setResourceId(1L);
        requestDto.setActionId(1L);

        webTestClient
                .post().uri("/v1/permissions/assign")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestDto)
                .exchange()
                .expectStatus().isForbidden();

        verifyNoInteractions(permissionApplicationService);
    }

    @Test
    @WithMockUser(roles = {"ADMINISTRADOR"})
    @DisplayName("POST /v1/permissions/assign should handle service errors gracefully")
    void assignPermission_ShouldReturn500_WhenServiceError() {
        AssignPermissionRequestDto requestDto = new AssignPermissionRequestDto();
        requestDto.setRoleId(1L);
        requestDto.setResourceId(1L);
        requestDto.setActionId(1L);

        when(permissionApplicationService.assignPermissionToRole(any(Permission.class)))
                .thenReturn(Mono.error(new RuntimeException("Database connection failed")));

        webTestClient
                .post().uri("/v1/permissions/assign")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestDto)
                .exchange()
                .expectStatus().is5xxServerError();

        verify(permissionApplicationService).assignPermissionToRole(any(Permission.class));
    }

    @Test
    @WithMockUser(roles = {"ADMINISTRADOR"})
    @DisplayName("POST /v1/permissions/assign should return 400 when roleId is null")
    void assignPermission_ShouldReturn400_WhenRoleIdIsNull() {
        // Simula que el validator lanza ConstraintViolationException
        when(validator.validate(any(AssignPermissionRequestDto.class)))
                .thenThrow(new ConstraintViolationException("Validation failed", new HashSet<>()));

        AssignPermissionRequestDto requestDto = new AssignPermissionRequestDto();
        requestDto.setRoleId(null);
        requestDto.setResourceId(1L);
        requestDto.setActionId(1L);

        webTestClient
                .post().uri("/v1/permissions/assign")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestDto)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error").isEqualTo("Validation failed");

        verifyNoInteractions(permissionApplicationService);
    }

    @Test
    @WithMockUser(roles = {"ADMINISTRADOR"})
    @DisplayName("POST /v1/permissions/assign should return 400 when all fields are null")
    void assignPermission_ShouldReturn400_WhenAllFieldsAreNull() {
        // Simula que el validator lanza ConstraintViolationException
        when(validator.validate(any(AssignPermissionRequestDto.class)))
                .thenThrow(new ConstraintViolationException("Validation failed", new HashSet<>()));

        AssignPermissionRequestDto requestDto = new AssignPermissionRequestDto();

        webTestClient
                .post().uri("/v1/permissions/assign")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestDto)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error").isEqualTo("Validation failed");

        verifyNoInteractions(permissionApplicationService);
    }

    @Test
    @WithMockUser(roles = {"ADMINISTRADOR"})
    @DisplayName("POST /v1/permissions/assign should return 400 when validation fails")
    void assignPermission_ShouldReturn400_WhenValidationFails() {
        // Simula que el validator lanza ConstraintViolationException
        when(validator.validate(any(AssignPermissionRequestDto.class)))
                .thenThrow(new ConstraintViolationException("Validation failed", new HashSet<>()));

        AssignPermissionRequestDto requestDto = new AssignPermissionRequestDto();

        webTestClient
                .post().uri("/v1/permissions/assign")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestDto)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error").isEqualTo("Validation failed");

        verifyNoInteractions(permissionApplicationService);
    }

}