package com.project.ticket.web.form;

import com.project.ticket.domain.auth.dto.LoginRequest;

public record LoginForm(String email, String password) {
    public LoginRequest toRequest() {
        return new LoginRequest(email, password);
    }
}
