package com.nitro.ms.neg.seguridad.gestion.domain.repository;

import com.nitro.ms.neg.seguridad.gestion.domain.model.Action;
import com.nitro.ms.neg.seguridad.gestion.domain.model.Permission;
import com.nitro.ms.neg.seguridad.gestion.domain.model.Resource;
import com.nitro.ms.neg.seguridad.gestion.domain.model.Role;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PermissionRepository {
    Flux<Permission> findByRoleId(Long roleId);
    //Mono<Permission> save(Permission permission);
    Mono<Permission> save(Permission permission, Role role, Resource resource, Action action); // <-- Nueva firma
    Mono<Void> delete(Permission permission);
    Mono<Boolean> exists(Long roleId, Long resourceId, Long actionId);
}
