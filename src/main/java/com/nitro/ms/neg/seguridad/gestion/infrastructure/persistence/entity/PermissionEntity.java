package com.nitro.ms.neg.seguridad.gestion.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "auth_permission")
public class PermissionEntity {

    @EmbeddedId
    private PermissionEntityId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("roleId") // Mapea el campo 'roleId' del EmbeddedId
    @JoinColumn(name = "role_id")
    private RoleEntity role;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("resourceId") // Mapea el campo 'resourceId' del EmbeddedId
    @JoinColumn(name = "resource_id")
    private ResourceEntity resource;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("actionId") // Mapea el campo 'actionId' del EmbeddedId
    @JoinColumn(name = "action_id")
    private ActionEntity action;

    @CreationTimestamp
    @Column(name = "granted_at", nullable = false, updatable = false)
    private LocalDateTime grantedAt;

    @Column(name = "granted_by")
    private Long grantedBy;
}