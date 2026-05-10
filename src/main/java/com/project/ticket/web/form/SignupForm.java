package com.project.ticket.web.form;

import com.project.ticket.domain.auth.dto.SignupRequest;
import com.project.ticket.domain.user.entity.UserRole;

public record SignupForm(
        String email,
        String password,
        String name,
        UserRole role
) {
    public SignupRequest toRequest() {
        return new SignupRequest(email, password, name, role);
    }
}
