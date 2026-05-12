package com.project.ticket.domain.auth.dto;

import com.project.ticket.domain.user.entity.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SignupRequest(
        @NotBlank @Email String email,
        @NotBlank String password,
        @NotBlank String name,
        UserRole role
) {
    public SignupRequest(String email, String password, String name) {
        this(email, password, name, UserRole.FAN);
    }

    public UserRole roleOrDefault() {
        return role == null ? UserRole.FAN : role;
    }
}
