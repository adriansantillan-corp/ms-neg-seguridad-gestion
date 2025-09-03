package com.nitro.ms.neg.seguridad.gestion.interfaces.rest;

import com.nitro.ms.neg.seguridad.gestion.application.service.UserApplicationService;
import com.nitro.ms.neg.seguridad.gestion.domain.model.User;
import com.nitro.ms.neg.seguridad.gestion.infrastructure.config.SecurityConfig;
import com.nitro.ms.neg.seguridad.gestion.interfaces.dto.response.UserResponseDto;
import com.nitro.ms.neg.seguridad.gestion.interfaces.mapper.UserApiMapper;
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

@WebFluxTest(UserController.class)
@Import({SecurityConfig.class, UserControllerTest.TestConfig.class})
class UserControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private UserApplicationService userApplicationService;

    @Autowired
    private UserApiMapper userApiMapper;

    @TestConfiguration
    static class TestConfig {

        @Bean
        @Primary
        UserApplicationService userApplicationService() {
            return mock(UserApplicationService.class);
        }

        @Bean
        @Primary
        UserApiMapper userApiMapper() {
            return mock(UserApiMapper.class);
        }
    }

    @Test
    @DisplayName("GET /v1/users should return 401 Unauthorized without authentication")
    void getAllUsers_ShouldReturn401_WhenNotAuthenticated() {
        webTestClient.get().uri("/v1/users")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    @WithMockUser
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
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.username").isEqualTo("test");
    }

    @Test
    @WithMockUser
    @DisplayName("GET /v1/users/{id} should return user when found")
    void getUserById_ShouldReturnUser_WhenFound() {
        User userDomain = User.builder().id(1L).username("test").build();
        UserResponseDto userDto = UserResponseDto.builder().id(1L).username("test").build();

        when(userApplicationService.findUserById(anyLong())).thenReturn(Mono.just(userDomain));
        when(userApiMapper.toDto(any(User.class))).thenReturn(userDto);

        webTestClient.get().uri("/v1/users/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserResponseDto.class)
                .isEqualTo(userDto);
    }
}