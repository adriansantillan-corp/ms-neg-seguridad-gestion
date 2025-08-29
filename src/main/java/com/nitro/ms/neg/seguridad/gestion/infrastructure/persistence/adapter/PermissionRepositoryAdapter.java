package com.nitro.ms.neg.seguridad.gestion.infrastructure.persistence.adapter;

import com.nitro.ms.neg.seguridad.gestion.domain.model.Action;
import com.nitro.ms.neg.seguridad.gestion.domain.model.Permission;
import com.nitro.ms.neg.seguridad.gestion.domain.model.Resource;
import com.nitro.ms.neg.seguridad.gestion.domain.model.Role;
import com.nitro.ms.neg.seguridad.gestion.domain.repository.PermissionRepository;
import com.nitro.ms.neg.seguridad.gestion.infrastructure.persistence.entity.*;
import com.nitro.ms.neg.seguridad.gestion.infrastructure.persistence.mapper.*;
import com.nitro.ms.neg.seguridad.gestion.infrastructure.persistence.repository.SpringDataPermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Component
@RequiredArgsConstructor
public class PermissionRepositoryAdapter implements PermissionRepository {

    private final SpringDataPermissionRepository jpaRepository;
    private final PermissionPersistenceMapper permissionMapper;
    private final RolePersistenceMapper roleMapper;
    private final ResourcePersistenceMapper resourceMapper;
    private final ActionPersistenceMapper actionMapper;

    @Override
    public Flux<Permission> findByRoleId(Long roleId) {
        return Flux.defer(() -> Flux.fromIterable(jpaRepository.findById_RoleId(roleId)))
                .subscribeOn(Schedulers.boundedElastic())
                .map(permissionMapper::toDomain);
    }

    @Override
    public Mono<Permission> save(Permission permission, Role role, Resource resource, Action action) {
        return Mono.fromCallable(() -> {
            // 1. Convertir los objetos de dominio de las relaciones a entidades JPA
            RoleEntity roleEntity = roleMapper.toEntity(role);
            ResourceEntity resourceEntity = resourceMapper.toEntity(resource);
            ActionEntity actionEntity = actionMapper.toEntity(action);

            // 2. Usar el mapper actualizado para crear la PermissionEntity con sus asociaciones
            PermissionEntity entityToSave = permissionMapper.toEntity(permission, roleEntity, resourceEntity, actionEntity);

            // 3. Guardar la entidad. JPA ahora tiene toda la información que necesita.
            PermissionEntity savedEntity = jpaRepository.save(entityToSave);
            return permissionMapper.toDomain(savedEntity);
        }).subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Void> delete(Permission permission) {
        // Para el delete, JPA puede funcionar solo con el ID, por lo que una versión simple del mapper es suficiente.
        PermissionEntityId id = new PermissionEntityId(permission.getRoleId(), permission.getResourceId(), permission.getActionId());
        PermissionEntity entityToDelete = new PermissionEntity();
        entityToDelete.setId(id);

        return Mono.fromRunnable(() -> jpaRepository.delete(entityToDelete))
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }

    @Override
    public Mono<Boolean> exists(Long roleId, Long resourceId, Long actionId) {
        return Mono.fromCallable(() -> {
            PermissionEntityId id = new PermissionEntityId(roleId, resourceId, actionId);
            return jpaRepository.existsById(id);
        }).subscribeOn(Schedulers.boundedElastic());
    }
}