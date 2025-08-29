package com.nitro.ms.neg.seguridad.gestion.infrastructure.persistence.repository;

import com.nitro.ms.neg.seguridad.gestion.infrastructure.persistence.entity.ModuleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataModuleRepository extends JpaRepository<ModuleEntity, Long> {
}
