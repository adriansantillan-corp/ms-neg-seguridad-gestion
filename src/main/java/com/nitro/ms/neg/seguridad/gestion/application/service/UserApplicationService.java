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
        if (user.getCognitoSub() == null) {
            return Mono.error(new IllegalArgumentException("cognitoSub cannot be null"));
        }

        return userRepository.findByCognitoSub(user.getCognitoSub())
                .flatMap(existingUser -> Mono.error(new IllegalArgumentException("User with cognitoSub already exists")))
                .switchIfEmpty(Mono.defer(() -> userRepository.save(user)))
                .cast(User.class);
    }
}