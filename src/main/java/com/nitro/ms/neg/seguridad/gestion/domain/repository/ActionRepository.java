package com.nitro.ms.neg.seguridad.gestion.domain.repository;

import com.nitro.ms.neg.seguridad.gestion.domain.model.Action;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ActionRepository {
    Mono<Action> findById(Long id);
    Mono<Action> findByActionName(String actionName); // Útil para validaciones únicas
    Flux<Action> findAll();
    Mono<Action> save(Action action);
    Mono<Void> deleteById(Long id);
}
