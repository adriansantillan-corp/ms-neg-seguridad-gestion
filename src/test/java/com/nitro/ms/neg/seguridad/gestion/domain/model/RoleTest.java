package com.nitro.ms.neg.seguridad.gestion.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class RoleTest {

    @Test
    @DisplayName("Role builder should create object with correct properties")
    void roleBuilder_ShouldCreateObjectCorrectly() {
        // Arrange
        Role role = Role.builder()
                .id(1L)
                .roleName("ADMIN")
                .description("Administrator Role")
                .countryCode("PE")
                .build();

        // Assert
        assertThat(role.getId()).isEqualTo(1L);
        assertThat(role.getRoleName()).isEqualTo("ADMIN");
        assertThat(role.getDescription()).isEqualTo("Administrator Role");
        assertThat(role.getCountryCode()).isEqualTo("PE");
    }
}