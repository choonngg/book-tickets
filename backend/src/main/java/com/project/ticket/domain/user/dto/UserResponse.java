package com.project.ticket.domain.user.dto;

import com.project.ticket.domain.user.entity.User;
import com.project.ticket.domain.user.entity.UserRole;

public record UserResponse(
        Long userId,
        String name,
        UserRole role
) {
    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getName(), user.getRole());
    }
}
