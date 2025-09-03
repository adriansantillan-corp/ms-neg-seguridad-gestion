package com.nitro.ms.neg.seguridad.gestion.interfaces.rest;

import com.nitro.ms.neg.seguridad.gestion.application.service.PermissionApplicationService;
import com.nitro.ms.neg.seguridad.gestion.domain.model.Permission;
import com.nitro.ms.neg.seguridad.gestion.infrastructure.config.SecurityConfig;
import com.nitro.ms.neg.seguridad.gestion.interfaces.dto.request.AssignPermissionRequestDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

@WebFluxTest(PermissionController.class)
@Import({SecurityConfig.class, PermissionControllerTest.TestConfig.class})
class PermissionControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private PermissionApplicationService permissionApplicationService;

    @TestConfiguration
    static class TestConfig {

        @Bean
        @Primary
        PermissionApplicationService permissionApplicationService() {
            return mock(PermissionApplicationService.class);
        }
    }

    @Test
    @DisplayName("POST /v1/permissions/assign should return 401 Unauthorized without authentication")
    void assignPermission_ShouldReturn401_WhenNotAuthenticated() {
        webTestClient.post().uri("/v1/permissions/assign")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    @WithMockUser
    @DisplayName("POST /v1/permissions/assign should return 201 Created when authenticated")
    void assignPermission_ShouldReturn201_WhenAuthenticated() {
        AssignPermissionRequestDto requestDto = new AssignPermissionRequestDto();
        requestDto.setRoleId(1L);
        requestDto.setResourceId(1L);
        requestDto.setActionId(1L);

        Permission permission = Permission.builder()
                .roleId(1L)
                .resourceId(1L)
                .actionId(1L)
                .build();

        when(permissionApplicationService.assignPermissionToRole(any(Permission.class)))
                .thenReturn(Mono.just(permission));

        webTestClient
                .mutateWith(csrf())
                .post().uri("/v1/permissions/assign")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestDto)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Permission.class)
                .isEqualTo(permission);
    }
}