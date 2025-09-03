package com.nitro.ms.neg.seguridad.gestion.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class PermissionTest {

    @Test
    @DisplayName("Permission builder should create object with correct properties")
    void permissionBuilder_ShouldCreateObjectCorrectly() {
        Permission permission = Permission.builder()
                .roleId(1L)
                .resourceId(2L)
                .actionId(3L)
                .build();

        assertThat(permission.getRoleId()).isEqualTo(1L);
        assertThat(permission.getResourceId()).isEqualTo(2L);
        assertThat(permission.getActionId()).isEqualTo(3L);
    }
}