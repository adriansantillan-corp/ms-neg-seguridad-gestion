package com.nitro.ms.neg.seguridad.gestion.interfaces.rest;

import com.nitro.ms.neg.seguridad.gestion.application.service.UserApplicationService;
import com.nitro.ms.neg.seguridad.gestion.domain.model.User;
import com.nitro.ms.neg.seguridad.gestion.infrastructure.config.TestSecurityConfig;
import com.nitro.ms.neg.seguridad.gestion.interfaces.dto.response.UserResponseDto;
import com.nitro.ms.neg.seguridad.gestion.interfaces.mapper.UserApiMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@WebFluxTest(UserController.class)
@Import({TestSecurityConfig.class, UserControllerTest.TestConfig.class})  // ← Centraliza toda la seguridad aquí
@ActiveProfiles("test")
class UserControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private UserApplicationService userApplicationService; // ← Inyectado desde TestConfig

    @Autowired
    private UserApiMapper userApiMapper; // ← Inyectado desde TestConfig

    @TestConfiguration
    static class TestConfig {

        @Bean
        @Primary
        public UserApplicationService userApplicationService() {
            return mock(UserApplicationService.class);
        }

        @Bean
        @Primary
        public UserApiMapper userApiMapper() {
            return mock(UserApiMapper.class);
        }
    }

    @BeforeEach
    void setUp() {
        reset(userApplicationService, userApiMapper);
    }

    @Test
    @DisplayName("GET /v1/users should return 401 Unauthorized without authentication")
    void getAllUsers_ShouldReturn401_WhenNotAuthenticated() {
        webTestClient.get().uri("/v1/users")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    @WithMockUser(roles = {"ADMINISTRADOR"})
    @DisplayName("GET /v1/users should return 200 OK with users when authenticated")
    void getAllUsers_ShouldReturn200_WhenAuthenticated() {
        User userDomain = User.builder().id(1L).username("test").build();
        UserResponseDto userDto = UserResponseDto.builder().id(1L).username("test").build();

        when(userApplicationService.findAllUsers()).thenReturn(Flux.just(userDomain));
        when(userApiMapper.toDto(any(User.class))).thenReturn(userDto);

        webTestClient.get().uri("/v1/users")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$").isArray()
                .jsonPath("$[0].id").isEqualTo(1)
                .jsonPath("$[0].username").isEqualTo("test");
    }

    @Test
    @WithMockUser(roles = {"SUPERVISOR"})
    @DisplayName("GET /v1/users/{id} should return user when found")
    void getUserById_ShouldReturnUser_WhenFound() {
        User userDomain = User.builder().id(1L).username("test").build();
        UserResponseDto userDto = UserResponseDto.builder().id(1L).username("test").build();

        when(userApplicationService.findUserById(anyLong())).thenReturn(Mono.just(userDomain));
        when(userApiMapper.toDto(any(User.class))).thenReturn(userDto);

        webTestClient.get().uri("/v1/users/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.username").isEqualTo("test");
    }


}