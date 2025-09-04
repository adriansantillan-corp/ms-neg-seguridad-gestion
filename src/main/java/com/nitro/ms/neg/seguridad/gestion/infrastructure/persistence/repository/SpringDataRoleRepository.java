package com.nitro.ms.neg.seguridad.gestion.infrastructure.persistence.repository;

import com.nitro.ms.neg.seguridad.gestion.infrastructure.persistence.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpringDataRoleRepository extends JpaRepository<RoleEntity, Long> {
    // Spring Data JPA generará automáticamente la consulta para este método
    boolean existsByRoleNameAndCountryCode(String roleName, String countryCode);

    // Ahora hacemos un JOIN con la propiedad 'users' de la RoleEntity, que es la
    // colección de UserEntity que mapeamos
    @Query("SELECT r FROM RoleEntity r JOIN r.users u WHERE u.id = :userId")
    List<RoleEntity> findRolesByUserId(@Param("userId") Long userId);
}