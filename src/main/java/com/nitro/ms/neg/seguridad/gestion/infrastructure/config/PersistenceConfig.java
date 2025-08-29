package com.nitro.ms.neg.seguridad.gestion.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing // Habilita la auditoría automática de campos como @CreatedDate, @LastModifiedDate
public class PersistenceConfig {
    // Aquí se pueden definir beans relacionados con la persistencia,
    // como un AuditorAware para la auditoría de @CreatedBy y @LastModifiedBy.
}
