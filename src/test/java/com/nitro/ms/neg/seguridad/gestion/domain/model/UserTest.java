package com.nitro.ms.neg.seguridad.gestion.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    @Test
    @DisplayName("User builder should create object with correct properties")
    void userBuilder_ShouldCreateObjectCorrectly() {
        User user = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@user.com")
                .cognitoSub("sub123")
                .enabled(true)
                .build();

        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getUsername()).isEqualTo("testuser");
        assertThat(user.getEmail()).isEqualTo("test@user.com");
        assertThat(user.getCognitoSub()).isEqualTo("sub123");
        assertThat(user.isEnabled()).isTrue();
    }
}