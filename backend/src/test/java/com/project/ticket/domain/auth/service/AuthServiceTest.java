package com.project.ticket.domain.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.project.ticket.domain.auth.dto.LoginRequest;
import com.project.ticket.domain.auth.dto.SignupRequest;
import com.project.ticket.global.exception.BusinessException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class AuthServiceTest {

    @Autowired
    AuthService authService;

    @Test
    void signupCreatesFanUser() {
        var response = authService.signup(new SignupRequest("fan@example.com", "password123", "Fan"));

        assertThat(response.email()).isEqualTo("fan@example.com");
        assertThat(response.name()).isEqualTo("Fan");
    }

    @Test
    void duplicateEmailFails() {
        authService.signup(new SignupRequest("fan@example.com", "password123", "Fan"));

        assertThatThrownBy(() -> authService.signup(new SignupRequest("fan@example.com", "password123", "Fan2")))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void loginReturnsBearerToken() {
        authService.signup(new SignupRequest("fan@example.com", "password123", "Fan"));

        var response = authService.login(new LoginRequest("fan@example.com", "password123"));

        assertThat(response.tokenType()).isEqualTo("Bearer");
        assertThat(response.accessToken()).isNotBlank();
        assertThat(response.refreshToken()).isNotBlank();
    }

    @Test
    void refreshReturnsBearerToken() {
        authService.signup(new SignupRequest("fan@example.com", "password123", "Fan"));
        var loginResponse = authService.login(new LoginRequest("fan@example.com", "password123"));

        var response = authService.refresh(loginResponse.refreshToken());

        assertThat(response.tokenType()).isEqualTo("Bearer");
        assertThat(response.accessToken()).isNotBlank();
    }
}
