package com.project.ticket.support;

import com.project.ticket.domain.auth.service.RefreshTokenStore;
import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("test")
public class InMemoryRefreshTokenStore implements RefreshTokenStore {
    private final Map<Long, String> refreshTokens = new ConcurrentHashMap<>();

    @Override
    public void save(Long userId, String refreshToken, Duration ttl) {
        refreshTokens.put(userId, refreshToken);
    }

    @Override
    public Optional<String> find(Long userId) {
        return Optional.ofNullable(refreshTokens.get(userId));
    }

    @Override
    public void delete(Long userId) {
        refreshTokens.remove(userId);
    }
}
