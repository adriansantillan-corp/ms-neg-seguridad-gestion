package com.nitro.ms.neg.seguridad.gestion.infrastructure.persistence.adapter;

import com.nitro.ms.neg.seguridad.gestion.domain.model.Action;
import com.nitro.ms.neg.seguridad.gestion.domain.repository.ActionRepository;
import com.nitro.ms.neg.seguridad.gestion.infrastructure.persistence.mapper.ActionPersistenceMapper;
import com.nitro.ms.neg.seguridad.gestion.infrastructure.persistence.repository.SpringDataActionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Component
@RequiredArgsConstructor
public class ActionRepositoryAdapter implements ActionRepository {

    private final SpringDataActionRepository jpaRepository;
    private final ActionPersistenceMapper mapper;

    @Override
    public Mono<Action> findById(Long id) {
        return Mono.fromCallable(() -> jpaRepository.findById(id))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(optionalEntity -> optionalEntity.map(mapper::toDomain).map(Mono::just).orElseGet(Mono::empty));
    }

    @Override
    public Mono<Action> findByActionName(String actionName) {
        return null;
    }

    @Override
    public Flux<Action> findAll() {
        return Flux.defer(() -> Flux.fromIterable(jpaRepository.findAll()))
                .subscribeOn(Schedulers.boundedElastic())
                .map(mapper::toDomain);
    }

    @Override
    public Mono<Action> save(Action action) {
        return Mono.fromCallable(() -> jpaRepository.save(mapper.toEntity(action)))
                .subscribeOn(Schedulers.boundedElastic())
                .map(mapper::toDomain);
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        return null;
    }
}