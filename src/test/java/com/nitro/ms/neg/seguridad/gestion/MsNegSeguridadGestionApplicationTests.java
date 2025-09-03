package com.nitro.ms.neg.seguridad.gestion;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers // Habilita la integración de JUnit 5 con Testcontainers
@ActiveProfiles("test") // Le dice a Spring que use el perfil 'test' (y application-test.yml)
class MsNegSeguridadGestionApplicationTests {

    // Crea un contenedor de PostgreSQL que vivirá durante la ejecución de las pruebas
    @Container
    static PostgreSQLContainer<?> postgresqlContainer = new PostgreSQLContainer<>("postgres:15-alpine");

    // Este método sobrescribe dinámicamente las propiedades de Spring ANTES de que la aplicación arranque.
    // Le decimos a la aplicación que se conecte a la base de datos del contenedor que acabamos de crear.
    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresqlContainer::getUsername);
        registry.add("spring.datasource.password", postgresqlContainer::getPassword);
        // Le decimos a Hibernate que cree el esquema en la base de datos de prueba al arrancar
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create");
    }

    @Test
    @DisplayName("Application context should load successfully")
    void contextLoads() {
        // Si esta prueba pasa, significa que:
        // 1. La aplicación puede arrancar.
        // 2. La conexión a la base de datos es exitosa.
        // 3. Todas las configuraciones (incluida SecurityConfig) son válidas.
        // Es la prueba de humo más importante del proyecto.
    }
}