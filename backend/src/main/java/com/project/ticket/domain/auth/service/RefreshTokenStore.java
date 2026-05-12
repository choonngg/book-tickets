package com.project.ticket.domain.auth.service;

import java.time.Duration;
import java.util.Optional;

public interface RefreshTokenStore {
    void save(Long userId, String refreshToken, Duration ttl);

    Optional<String> find(Long userId);

    void delete(Long userId);
}
