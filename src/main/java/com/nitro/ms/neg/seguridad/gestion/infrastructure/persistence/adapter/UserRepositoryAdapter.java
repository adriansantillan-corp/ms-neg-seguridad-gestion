package com.nitro.ms.neg.seguridad.gestion.infrastructure.persistence.adapter;

import com.nitro.ms.neg.seguridad.gestion.domain.model.User;
import com.nitro.ms.neg.seguridad.gestion.domain.repository.UserRepository;
import com.nitro.ms.neg.seguridad.gestion.infrastructure.persistence.entity.UserEntity;
import com.nitro.ms.neg.seguridad.gestion.infrastructure.persistence.mapper.UserPersistenceMapper;
import com.nitro.ms.neg.seguridad.gestion.infrastructure.persistence.repository.SpringDataUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Component
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepository {

    private final SpringDataUserRepository jpaRepository;
    private final UserPersistenceMapper mapper;

    @Override
    public Mono<User> findById(Long id) {
        return Mono.fromCallable(() -> jpaRepository.findById(id))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(optionalEntity -> optionalEntity.map(mapper::toDomain).map(Mono::just).orElseGet(Mono::empty));
    }

    @Override
    public Mono<User> findByCognitoSub(String cognitoSub) {
        return Mono.fromCallable(() -> jpaRepository.findByCognitoSub(cognitoSub))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(optionalEntity -> optionalEntity.map(mapper::toDomain).map(Mono::just).orElseGet(Mono::empty));
    }

    @Override
    public Flux<User> findAll() {
        return Flux.defer(() -> Flux.fromIterable(jpaRepository.findAll()))
                .subscribeOn(Schedulers.boundedElastic())
                .map(mapper::toDomain);
    }

    @Override
    public Mono<User> save(User user) {
        return Mono.fromCallable(() -> {
            UserEntity entity = mapper.toEntity(user);
            UserEntity savedEntity = jpaRepository.save(entity);
            return mapper.toDomain(savedEntity);
        }).subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        return Mono.fromRunnable(() -> jpaRepository.deleteById(id))
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }
}