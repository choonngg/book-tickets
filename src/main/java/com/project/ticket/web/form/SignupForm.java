package com.project.ticket.web.form;

import com.project.ticket.domain.auth.dto.SignupRequest;
import com.project.ticket.domain.user.entity.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SignupForm(
        @NotBlank(message = "이메일을 입력해주세요.")
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        String email,
        @NotBlank(message = "비밀번호를 입력해주세요.")
        String password,
        @NotBlank(message = "이름을 입력해주세요.")
        String name,
        @NotNull(message = "역할을 선택해주세요.")
        UserRole role
) {
    public SignupRequest toRequest() {
        return new SignupRequest(email, password, name, role);
    }
}
