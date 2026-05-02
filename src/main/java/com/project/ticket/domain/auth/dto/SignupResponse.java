package com.project.ticket.domain.auth.dto;

import com.project.ticket.domain.auth.entity.Login;

public record SignupResponse(Long userId, String email, String name) {
    public static SignupResponse from(Login login) {
        return new SignupResponse(
                login.getUser().getId(),
                login.getEmail(),
                login.getUser().getName()
        );
    }
}
