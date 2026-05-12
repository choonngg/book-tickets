package com.project.ticket.domain.auth.entity;

import static org.assertj.core.api.Assertions.assertThat;

import com.project.ticket.domain.user.entity.User;
import org.junit.jupiter.api.Test;

class LoginTest {
    @Test
    void createEmailPasswordLogin() {
        User user = User.createFan("Fan");

        Login login = Login.create(user, "fan@example.com", "encoded-password");

        assertThat(login.getUser()).isSameAs(user);
        assertThat(login.getEmail()).isEqualTo("fan@example.com");
        assertThat(login.getPassword()).isEqualTo("encoded-password");
    }
}
