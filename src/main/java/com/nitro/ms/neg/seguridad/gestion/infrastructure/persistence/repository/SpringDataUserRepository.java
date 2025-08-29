package com.nitro.ms.neg.seguridad.gestion.infrastructure.persistence.repository;

import com.nitro.ms.neg.seguridad.gestion.infrastructure.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SpringDataUserRepository extends JpaRepository<UserEntity, Long> {
    // Spring Data JPA generará automáticamente la consulta para este método
    Optional<UserEntity> findByCognitoSub(String cognitoSub);
}