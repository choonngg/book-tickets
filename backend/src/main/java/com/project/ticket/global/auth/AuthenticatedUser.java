package com.project.ticket.global.auth;

import com.project.ticket.domain.user.entity.UserRole;

public record AuthenticatedUser(Long userId, UserRole role) {
}
