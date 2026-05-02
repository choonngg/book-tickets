package com.project.ticket.domain.auth.service;

import com.project.ticket.global.auth.JwtProvider;
import com.project.ticket.global.exception.BusinessException;
import com.project.ticket.global.exception.ErrorCode;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RefreshTokenService {
    private final JwtProvider jwtProvider;
    private final RefreshTokenStore refreshTokenStore;
    private final long refreshTokenDays;

    public RefreshTokenService(
            JwtProvider jwtProvider,
            RefreshTokenStore refreshTokenStore,
            @Value("${jwt.refresh-token-days}") long refreshTokenDays
    ) {
        this.jwtProvider = jwtProvider;
        this.refreshTokenStore = refreshTokenStore;
        this.refreshTokenDays = refreshTokenDays;
    }

    public String issue(Long userId) {
        String refreshToken = jwtProvider.createRefreshToken(userId);
        refreshTokenStore.save(userId, refreshToken, Duration.ofDays(refreshTokenDays));
        return refreshToken;
    }

    public Long validate(String refreshToken) {
        Long userId = jwtProvider.parseUserId(refreshToken);
        String storedToken = refreshTokenStore.find(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.UNAUTHORIZED));
        if (!storedToken.equals(refreshToken)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
        return userId;
    }

    public void delete(Long userId) {
        refreshTokenStore.delete(userId);
    }
}
