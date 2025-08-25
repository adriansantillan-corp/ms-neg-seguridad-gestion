package com.nitro.ms.neg.seguridad.gestion.domain.repository;

import com.nitro.ms.neg.seguridad.gestion.domain.model.Role;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RoleRepository {
    Mono<Role> findById(Long id);
    Flux<Role> findAll();
    Mono<Role> save(Role role);
    Mono<Void> deleteById(Long id);
    Mono<Boolean> existsByRoleNameAndCountryCode(String roleName, String countryCode);
}