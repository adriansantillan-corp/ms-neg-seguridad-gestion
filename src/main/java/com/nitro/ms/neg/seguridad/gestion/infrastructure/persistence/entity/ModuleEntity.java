package com.nitro.ms.neg.seguridad.gestion.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
//... otras importaciones

@Getter
@Setter
@Entity
@Table(name = "auth_module")
public class ModuleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "module_id")
    private Long id;

    @Column(name = "module_name", unique = true, nullable = false, length = 100)
    private String moduleName;
    //... otros campos como description, createdBy, etc.
}