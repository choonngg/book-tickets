package com.project.ticket.domain.user.controller;

import com.project.ticket.domain.user.dto.UserResponse;
import com.project.ticket.domain.user.service.UserService;
import com.project.ticket.global.auth.AuthenticatedUser;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public UserResponse findMe(@AuthenticationPrincipal AuthenticatedUser user) {
        return userService.findMe(user.userId());
    }
}
