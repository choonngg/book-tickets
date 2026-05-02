package com.project.ticket.domain.auth.service;

import com.project.ticket.domain.auth.dto.LoginRequest;
import com.project.ticket.domain.auth.dto.LoginResponse;
import com.project.ticket.domain.auth.dto.SignupRequest;
import com.project.ticket.domain.auth.dto.SignupResponse;
import com.project.ticket.domain.auth.dto.TokenRefreshResponse;
import com.project.ticket.domain.auth.entity.Login;
import com.project.ticket.domain.auth.repository.LoginRepository;
import com.project.ticket.domain.user.entity.User;
import com.project.ticket.domain.user.repository.UserRepository;
import com.project.ticket.global.auth.JwtProvider;
import com.project.ticket.global.exception.BusinessException;
import com.project.ticket.global.exception.ErrorCode;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final LoginRepository loginRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;

    public AuthService(
            UserRepository userRepository,
            LoginRepository loginRepository,
            PasswordEncoder passwordEncoder,
            JwtProvider jwtProvider,
            RefreshTokenService refreshTokenService
    ) {
        this.userRepository = userRepository;
        this.loginRepository = loginRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
        this.refreshTokenService = refreshTokenService;
    }

    @Transactional
    public SignupResponse signup(SignupRequest request) {
        if (loginRepository.existsByEmail(request.email())) {
            throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);
        }
        User user = userRepository.save(User.createFan(request.name()));
        Login login = loginRepository.save(Login.email(
                user,
                request.email(),
                passwordEncoder.encode(request.password())
        ));
        return SignupResponse.from(login);
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        Login login = loginRepository.findByEmail(request.email())
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_CREDENTIALS));
        if (!passwordEncoder.matches(request.password(), login.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_CREDENTIALS);
        }
        String accessToken = jwtProvider.createAccessToken(login.getUser().getId(), login.getUser().getRole());
        String refreshToken = refreshTokenService.issue(login.getUser().getId());
        return LoginResponse.bearer(accessToken, refreshToken);
    }

    @Transactional(readOnly = true)
    public TokenRefreshResponse refresh(String refreshToken) {
        Long userId = refreshTokenService.validate(refreshToken);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        String accessToken = jwtProvider.createAccessToken(user.getId(), user.getRole());
        return TokenRefreshResponse.bearer(accessToken);
    }

    public void logout(String refreshToken) {
        Long userId = refreshTokenService.validate(refreshToken);
        refreshTokenService.delete(userId);
    }
}
