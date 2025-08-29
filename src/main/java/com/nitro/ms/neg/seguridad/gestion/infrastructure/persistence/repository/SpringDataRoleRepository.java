package com.nitro.ms.neg.seguridad.gestion.infrastructure.persistence.repository;

import com.nitro.ms.neg.seguridad.gestion.infrastructure.persistence.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataRoleRepository extends JpaRepository<RoleEntity, Long> {
    // Spring Data JPA generará automáticamente la consulta para este método
    boolean existsByRoleNameAndCountryCode(String roleName, String countryCode);
}