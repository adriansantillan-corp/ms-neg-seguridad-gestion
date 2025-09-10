package com.nitro.ms.neg.seguridad.gestion.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "auth_user", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"country_code", "username"}),
        @UniqueConstraint(columnNames = {"country_code", "email"})
})
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "cognito_sub", unique = true, nullable = false)
    private String cognitoSub;

    @Column(name = "user_principal_name")
    private String userPrincipalName;

    @Column(name = "username", nullable = false, length = 100)
    private String username;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "first_name", length = 100)
    private String firstName;

    @Column(name = "last_name", length = 100)
    private String lastName;

    @Column(name = "phone", length = 50)
    private String phone;

    @Column(name = "country_code", length = 5)
    private String countryCode;

    @Column(name = "profile_data", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private String profileData;

    @Column(nullable = false)
    private boolean enabled = true;

    @Column(nullable = false)
    private boolean accountNonLocked = true;

    /*@Column(nullable = false)
    private boolean credentialsNonExpired = true;*/

    @Column(nullable = false)
    private boolean accountNonExpired = true;

    private LocalDateTime lastLogin;

    private Long createdBy;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private Long updatedBy;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // Define la relación muchos-a-muchos con RoleEntity.
    @ManyToMany(fetch = FetchType.LAZY) // LAZY es mejor para el rendimiento general
    @JoinTable(
            name = "auth_user_role", // El nombre de la tabla de unión en la base de datos
            joinColumns = @JoinColumn(name = "user_id"), // La columna en la tabla de unión que apunta a esta entidad (User)
            inverseJoinColumns = @JoinColumn(name = "role_id") // La columna que apunta a la otra entidad (Role)
    )
    private Set<RoleEntity> roles = new HashSet<>();
}