package com.nitro.ms.neg.seguridad.gestion.application.service;

import com.nitro.ms.neg.seguridad.gestion.domain.model.User;
import com.nitro.ms.neg.seguridad.gestion.domain.repository.UserRepository;
import com.nitro.ms.neg.seguridad.gestion.infrastructure.exception.specific_exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserApplicationService {

    private final UserRepository userRepository;

    public Mono<User> findUserById(Long id) {
        return userRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("User not found with id: " + id)));
    }

    public Flux<User> findAllUsers() {
        return userRepository.findAll();
    }

    public Mono<User> createUser(User user) {
        // Lógica de negocio: verificar si ya existe un usuario con ese cognito_sub
        return userRepository.findByCognitoSub(user.getCognitoSub())
                .flatMap(existingUser -> Mono.error(new IllegalArgumentException("User with this Cognito SUB already exists.")))
                .switchIfEmpty(userRepository.save(user))
                .cast(User.class);
    }
}