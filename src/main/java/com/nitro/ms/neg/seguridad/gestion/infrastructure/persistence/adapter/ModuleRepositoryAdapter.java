package com.nitro.ms.neg.seguridad.gestion.infrastructure.persistence.adapter;

import com.nitro.ms.neg.seguridad.gestion.domain.model.Module;
import com.nitro.ms.neg.seguridad.gestion.domain.repository.ModuleRepository;
import com.nitro.ms.neg.seguridad.gestion.infrastructure.persistence.mapper.ModulePersistenceMapper;
import com.nitro.ms.neg.seguridad.gestion.infrastructure.persistence.repository.SpringDataModuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Component
@RequiredArgsConstructor
public class ModuleRepositoryAdapter implements ModuleRepository {

    private final SpringDataModuleRepository jpaRepository;
    private final ModulePersistenceMapper mapper;

    @Override
    public Mono<Module> findById(Long id) {
        return Mono.fromCallable(() -> jpaRepository.findById(id))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(optionalEntity -> optionalEntity.map(mapper::toDomain).map(Mono::just).orElseGet(Mono::empty));
    }

    @Override
    public Mono<Module> findByModuleName(String moduleName) {
        return null;
    }

    @Override
    public Flux<Module> findAll() {
        return Flux.defer(() -> Flux.fromIterable(jpaRepository.findAll()))
                .subscribeOn(Schedulers.boundedElastic())
                .map(mapper::toDomain);
    }

    @Override
    public Mono<Module> save(Module module) {
        return Mono.fromCallable(() -> jpaRepository.save(mapper.toEntity(module)))
                .subscribeOn(Schedulers.boundedElastic())
                .map(mapper::toDomain);
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        return null;
    }
}