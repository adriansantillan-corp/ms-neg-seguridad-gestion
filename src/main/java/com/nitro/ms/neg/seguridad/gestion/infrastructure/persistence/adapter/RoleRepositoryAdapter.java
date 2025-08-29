package com.nitro.ms.neg.seguridad.gestion.infrastructure.persistence.adapter;

import com.nitro.ms.neg.seguridad.gestion.domain.model.Role;
import com.nitro.ms.neg.seguridad.gestion.domain.repository.RoleRepository;
import com.nitro.ms.neg.seguridad.gestion.infrastructure.persistence.entity.RoleEntity;
import com.nitro.ms.neg.seguridad.gestion.infrastructure.persistence.mapper.RolePersistenceMapper;
import com.nitro.ms.neg.seguridad.gestion.infrastructure.persistence.repository.SpringDataRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Component
@RequiredArgsConstructor
public class RoleRepositoryAdapter implements RoleRepository {

    private final SpringDataRoleRepository jpaRepository;
    private final RolePersistenceMapper mapper;

    @Override
    public Mono<Role> findById(Long id) {
        return Mono.fromCallable(() -> jpaRepository.findById(id))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(optionalEntity -> optionalEntity.map(mapper::toDomain).map(Mono::just).orElseGet(Mono::empty));
    }

    @Override
    public Flux<Role> findAll() {
        return Flux.defer(() -> Flux.fromIterable(jpaRepository.findAll()))
                .subscribeOn(Schedulers.boundedElastic())
                .map(mapper::toDomain);
    }

    @Override
    public Mono<Role> save(Role role) {
        return Mono.fromCallable(() -> {
            RoleEntity entity = mapper.toEntity(role);
            RoleEntity savedEntity = jpaRepository.save(entity);
            return mapper.toDomain(savedEntity);
        }).subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        return Mono.fromRunnable(() -> jpaRepository.deleteById(id))
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }

    @Override
    public Mono<Boolean> existsByRoleNameAndCountryCode(String roleName, String countryCode) {
        return Mono.fromCallable(() -> jpaRepository.existsByRoleNameAndCountryCode(roleName, countryCode))
                .subscribeOn(Schedulers.boundedElastic());
    }
}