package com.nitro.ms.neg.seguridad.gestion.domain.repository;

import com.nitro.ms.neg.seguridad.gestion.domain.model.Permission;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PermissionRepository {
    Flux<Permission> findByRoleId(Long roleId);
    Mono<Permission> save(Permission permission);
    Mono<Void> delete(Permission permission);
    Mono<Boolean> exists(Long roleId, Long resourceId, Long actionId);
}
