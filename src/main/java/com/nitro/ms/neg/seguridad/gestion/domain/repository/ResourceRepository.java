package com.nitro.ms.neg.seguridad.gestion.domain.repository;

import com.nitro.ms.neg.seguridad.gestion.domain.model.Resource;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ResourceRepository {
    Mono<Resource> findById(Long id);
    Flux<Resource> findByModuleId(Long moduleId); // Búsqueda por módulo (relación común)
    Flux<Resource> findAll();
    Mono<Resource> save(Resource resource);
    Mono<Void> deleteById(Long id);
}
