package com.nitro.ms.neg.seguridad.gestion.domain.repository;

import com.nitro.ms.neg.seguridad.gestion.domain.model.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserRepository {
    Mono<User> findById(Long id);
    Mono<User> findByCognitoSub(String cognitoSub);
    Flux<User> findAll();
    Mono<User> save(User user);
    Mono<Void> deleteById(Long id);
}
