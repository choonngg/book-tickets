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
        step("회원가입 요청을 준비한다.");
        var response = authService.signup(new SignupRequest("fan@example.com", "password123", "Fan"));

        step("생성된 사용자 이메일과 이름을 검증한다.");
        assertThat(response.email()).isEqualTo("fan@example.com");
        assertThat(response.name()).isEqualTo("Fan");
    }

    @Test
    void duplicateEmailFails() {
        step("첫 번째 회원가입으로 이메일을 선점한다.");
        authService.signup(new SignupRequest("fan@example.com", "password123", "Fan"));

        step("같은 이메일로 다시 가입하면 비즈니스 예외가 발생하는지 검증한다.");
        assertThatThrownBy(() -> authService.signup(new SignupRequest("fan@example.com", "password123", "Fan2")))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void loginReturnsBearerToken() {
        step("로그인할 사용자를 회원가입시킨다.");
        authService.signup(new SignupRequest("fan@example.com", "password123", "Fan"));

        step("이메일과 비밀번호로 로그인한다.");
        var response = authService.login(new LoginRequest("fan@example.com", "password123"));

        step("access token, refresh token, token type을 검증한다.");
        assertThat(response.tokenType()).isEqualTo("Bearer");
        assertThat(response.accessToken()).isNotBlank();
        assertThat(response.refreshToken()).isNotBlank();
    }

    @Test
    void refreshReturnsBearerToken() {
        step("refresh token을 얻기 위해 회원가입 후 로그인한다.");
        authService.signup(new SignupRequest("fan@example.com", "password123", "Fan"));
        var loginResponse = authService.login(new LoginRequest("fan@example.com", "password123"));

        step("refresh token으로 새 access token을 발급받는다.");
        var response = authService.refresh(loginResponse.refreshToken());

        step("재발급 응답의 bearer token을 검증한다.");
        assertThat(response.tokenType()).isEqualTo("Bearer");
        assertThat(response.accessToken()).isNotBlank();
    }

    private void step(String message) {
        System.out.println("[TEST STEP] " + message);
    }
}
