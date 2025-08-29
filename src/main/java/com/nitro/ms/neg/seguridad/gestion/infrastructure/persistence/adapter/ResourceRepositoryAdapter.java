package com.nitro.ms.neg.seguridad.gestion.infrastructure.persistence.adapter;

import com.nitro.ms.neg.seguridad.gestion.domain.model.Resource;
import com.nitro.ms.neg.seguridad.gestion.domain.repository.ResourceRepository;
import com.nitro.ms.neg.seguridad.gestion.infrastructure.persistence.mapper.ResourcePersistenceMapper;
import com.nitro.ms.neg.seguridad.gestion.infrastructure.persistence.repository.SpringDataResourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Component
@RequiredArgsConstructor
public class ResourceRepositoryAdapter implements ResourceRepository {

    private final SpringDataResourceRepository jpaRepository;
    private final ResourcePersistenceMapper mapper;

    @Override
    public Mono<Resource> findById(Long id) {
        return Mono.fromCallable(() -> jpaRepository.findById(id))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(optionalEntity -> optionalEntity.map(mapper::toDomain).map(Mono::just).orElseGet(Mono::empty));
    }

    @Override
    public Flux<Resource> findByModuleId(Long moduleId) {
        return null;
    }

    @Override
    public Flux<Resource> findAll() {
        return Flux.defer(() -> Flux.fromIterable(jpaRepository.findAll()))
                .subscribeOn(Schedulers.boundedElastic())
                .map(mapper::toDomain);
    }

    @Override
    public Mono<Resource> save(Resource resource) {
        // La lógica para establecer la entidad Module se manejaría en el ApplicationService
        return Mono.fromCallable(() -> jpaRepository.save(mapper.toEntity(resource)))
                .subscribeOn(Schedulers.boundedElastic())
                .map(mapper::toDomain);
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        return null;
    }
}