package com.nitro.ms.neg.seguridad.gestion.domain.repository;

import com.nitro.ms.neg.seguridad.gestion.domain.model.Module;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ModuleRepository {
    Mono<Module> findById(Long id);
    Mono<Module> findByModuleName(String moduleName); // Para evitar duplicados
    Flux<Module> findAll();
    Mono<Module> save(Module module);
    Mono<Void> deleteById(Long id);
}