package com.project.ticket.global.auth;

import static org.assertj.core.api.Assertions.assertThat;

import com.project.ticket.domain.user.entity.UserRole;
import org.junit.jupiter.api.Test;

class JwtProviderTest {

    @Test
    void createsAndParsesAccessToken() {
        JwtProvider provider = new JwtProvider(
                "local-development-secret-key-must-be-at-least-32-bytes",
                30,
                7
        );

        String token = provider.createAccessToken(1L, UserRole.FAN);
        AuthenticatedUser user = provider.parse(token);

        assertThat(user.userId()).isEqualTo(1L);
        assertThat(user.role()).isEqualTo(UserRole.FAN);
    }
}
