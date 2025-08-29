package com.nitro.ms.neg.seguridad.gestion.infrastructure.persistence.repository;

import com.nitro.ms.neg.seguridad.gestion.infrastructure.persistence.entity.ActionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataActionRepository extends JpaRepository<ActionEntity, Long> {
}