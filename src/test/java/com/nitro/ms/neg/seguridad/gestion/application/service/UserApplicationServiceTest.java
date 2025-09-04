// El código completo que te proporcioné en la respuesta anterior va aquí.
// Es la versión final y correcta.
package com.nitro.ms.neg.seguridad.gestion.application.service;

import com.nitro.ms.neg.seguridad.gestion.domain.model.User;
import com.nitro.ms.neg.seguridad.gestion.domain.repository.UserRepository;
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
class UserApplicationServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserApplicationService userApplicationService;

    private User user1;

    @BeforeEach
    void setUp() {
        user1 = User.builder()
                .id(1L)
                .cognitoSub("sub123")
                .username("testuser")
                .email("test@example.com")
                .build();
    }

    @Test
    @DisplayName("findUserById should return user when found")
    void findUserById_ShouldReturnUser_WhenFound() {
        when(userRepository.findById(anyLong())).thenReturn(Mono.just(user1));
        StepVerifier.create(userApplicationService.findUserById(1L))
                .expectNextMatches(foundUser -> foundUser.getUsername().equals("testuser"))
                .verifyComplete();
    }

    @Test
    @DisplayName("findUserById should return error when not found")
    void findUserById_ShouldReturnError_WhenNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Mono.empty());
        StepVerifier.create(userApplicationService.findUserById(99L))
                .expectError(ResourceNotFoundException.class)
                .verify();
    }

    @Test
    @DisplayName("createUser should save and return user when cognitoSub does not exist")
    void createUser_ShouldSaveAndReturnUser_WhenCognitoSubDoesNotExist() {
        when(userRepository.findByCognitoSub(user1.getCognitoSub())).thenReturn(Mono.empty());
        when(userRepository.save(any(User.class))).thenReturn(Mono.just(user1));
        StepVerifier.create(userApplicationService.createUser(user1))
                .expectNext(user1)
                .verifyComplete();
    }

    @Test
    @DisplayName("createUser should return error when cognitoSub already exists")
    void createUser_ShouldReturnError_WhenCognitoSubExists() {
        // Arrange: El usuario con este cognitoSub YA existe.
        // Usamos el objeto 'user1' de la instancia para configurar el mock.
        when(userRepository.findByCognitoSub(user1.getCognitoSub())).thenReturn(Mono.just(user1));

        // Act: Pasamos exactamente el mismo objeto 'user1' al método.
        Mono<User> result = userApplicationService.createUser(user1);

        // Assert: Verificamos que se emite un error de tipo IllegalArgumentException.
        StepVerifier.create(result)
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    @DisplayName("findAllUsers should return all users")
    void findAllUsers_ShouldReturnAllUsers() {
        User user2 = User.builder().id(2L).username("testuser2").build();
        when(userRepository.findAll()).thenReturn(Flux.just(user1, user2));
        StepVerifier.create(userApplicationService.findAllUsers())
                .expectNext(user1)
                .expectNext(user2)
                .verifyComplete();
    }
}