package com.nitro.ms.neg.seguridad.gestion.interfaces.rest;

import com.nitro.ms.neg.seguridad.gestion.application.service.RoleApplicationService;
import com.nitro.ms.neg.seguridad.gestion.domain.model.Role;
import com.nitro.ms.neg.seguridad.gestion.infrastructure.config.SecurityConfig;
import com.nitro.ms.neg.seguridad.gestion.interfaces.dto.response.RoleResponseDto;
import com.nitro.ms.neg.seguridad.gestion.interfaces.mapper.RoleApiMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;

@WebFluxTest(RoleController.class)
@Import({SecurityConfig.class, RoleControllerTest.TestConfig.class})
class RoleControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private RoleApplicationService roleApplicationService;

    @Autowired
    private RoleApiMapper roleApiMapper;

    @TestConfiguration
    static class TestConfig {

        @Bean
        @Primary // Asegura que este mock reemplace cualquier otro bean de este tipo
        RoleApplicationService roleApplicationService() {
            return mock(RoleApplicationService.class);
        }

        @Bean
        @Primary
        RoleApiMapper roleApiMapper() {
            return mock(RoleApiMapper.class);
        }
    }

    @Test
    @DisplayName("GET /v1/roles should return 401 Unauthorized without authentication")
    void getAllRoles_ShouldReturn401_WhenNotAuthenticated() {
        webTestClient.get().uri("/v1/roles")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    @WithMockUser
    @DisplayName("GET /v1/roles should return 200 OK when authenticated")
    void getAllRoles_ShouldReturn200_WhenAuthenticated() {
        Role roleDomain = Role.builder().id(1L).roleName("VENDEDOR").build();
        RoleResponseDto roleDto = RoleResponseDto.builder().id(1L).roleName("VENDEDOR").build();

        when(roleApplicationService.findAllRoles()).thenReturn(Flux.just(roleDomain));
        when(roleApiMapper.toDto(any(Role.class))).thenReturn(roleDto);

        webTestClient.get().uri("/v1/roles")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.roleName").isEqualTo("VENDEDOR");
    }

    @Test
    @WithMockUser
    @DisplayName("GET /v1/roles/{id} should return role when found")
    void getRoleById_ShouldReturnRole_WhenFound() {
        Role roleDomain = Role.builder().id(1L).roleName("VENDEDOR").build();
        RoleResponseDto roleDto = RoleResponseDto.builder().id(1L).roleName("VENDEDOR").build();

        when(roleApplicationService.findRoleById(anyLong())).thenReturn(Mono.just(roleDomain));
        when(roleApiMapper.toDto(any(Role.class))).thenReturn(roleDto);

        webTestClient.get().uri("/v1/roles/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(RoleResponseDto.class)
                .isEqualTo(roleDto);
    }
}