package com.nitro.ms.neg.seguridad.gestion.infrastructure.persistence.repository;

import com.nitro.ms.neg.seguridad.gestion.infrastructure.persistence.entity.PermissionEntity;
import com.nitro.ms.neg.seguridad.gestion.infrastructure.persistence.entity.PermissionEntityId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpringDataPermissionRepository extends JpaRepository<PermissionEntity, PermissionEntityId> {
    // Spring Data generará la consulta para buscar todos los permisos por el ID del rol
    List<PermissionEntity> findById_RoleId(Long roleId);
}