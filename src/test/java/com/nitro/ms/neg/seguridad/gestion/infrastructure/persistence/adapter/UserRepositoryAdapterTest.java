package com.nitro.ms.neg.seguridad.gestion.infrastructure.persistence.adapter;

import com.nitro.ms.neg.seguridad.gestion.infrastructure.persistence.entity.RoleEntity;
import com.nitro.ms.neg.seguridad.gestion.infrastructure.persistence.entity.UserEntity;
import com.nitro.ms.neg.seguridad.gestion.infrastructure.persistence.repository.SpringDataRoleRepository;
import com.nitro.ms.neg.seguridad.gestion.infrastructure.persistence.repository.SpringDataUserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest // Carga solo la configuración de persistencia de Spring.
@Testcontainers
class UserRepositoryAdapterTest {

    @Container
    static PostgreSQLContainer<?> postgresqlContainer = new PostgreSQLContainer<>("postgres:15-alpine");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresqlContainer::getUsername);
        registry.add("spring.datasource.password", postgresqlContainer::getPassword);
        // Para @DataJpaTest, es común dejar que Hibernate cree el esquema.
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @Autowired
    private SpringDataUserRepository userRepository;

    @Autowired
    private SpringDataRoleRepository roleRepository;

    @Test
    @DisplayName("should save user with roles and retrieve them correctly using custom query")
    void saveUserWithRoles_and_findRolesByUserId_ShouldWork() {
        // Arrange: Crear y guardar roles
        RoleEntity roleVendedor = new RoleEntity();
        roleVendedor.setRoleName("VENDEDOR");
        roleVendedor.setCountryCode("PE");
        roleRepository.save(roleVendedor);

        RoleEntity roleSupervisor = new RoleEntity();
        roleSupervisor.setRoleName("SUPERVISOR");
        roleSupervisor.setCountryCode("PE");
        roleRepository.save(roleSupervisor);

        // Arrange: Crear un usuario y asignarle los roles
        UserEntity userToSave = new UserEntity();
        userToSave.setCognitoSub("integration-test-sub-123");
        userToSave.setUsername("itest");
        userToSave.setEmail("itest@nitro.com");
        userToSave.setRoles(Set.of(roleVendedor, roleSupervisor));

        // Act: Guardar el usuario (JPA se encarga de la tabla de unión)
        UserEntity savedUser = userRepository.save(userToSave);
        assertThat(savedUser.getId()).isNotNull();

        // Act: Usar nuestra consulta personalizada para buscar los roles del usuario guardado
        List<RoleEntity> foundRoles = roleRepository.findRolesByUserId(savedUser.getId());

        // Assert
        assertThat(foundRoles).isNotNull();
        assertThat(foundRoles).hasSize(2);
        assertThat(foundRoles).extracting(RoleEntity::getRoleName)
                .containsExactlyInAnyOrder("VENDEDOR", "SUPERVISOR");
    }
}