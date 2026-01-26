package com.linkbharat.linkbharatbackend.service.impl;

import com.linkbharat.linkbharatbackend.domain.entity.AuthUser;
import com.linkbharat.linkbharatbackend.domain.entity.RefreshToken;
import com.linkbharat.linkbharatbackend.repository.AuthUserRepository;
import com.linkbharat.linkbharatbackend.repository.RefreshTokenRepository;
import com.linkbharat.linkbharatbackend.service.RefreshTokenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthUserRepository authUserRepository;

    @Value("${app.jwt.refresh-token-expiration-minutes:43200}")
    private long refreshTokenExpirationMinutes;

    public RefreshTokenServiceImpl(RefreshTokenRepository refreshTokenRepository,
                                   AuthUserRepository authUserRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.authUserRepository = authUserRepository;
    }

    @Override
    @Transactional
    public RefreshToken createRefreshToken(AuthUser authUser) {
        refreshTokenRepository.findByAuthUserAndRevokedFalse(authUser)
                .ifPresent(existing -> {
                    existing.setRevoked(true);
                    refreshTokenRepository.save(existing);
                });

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setAuthUser(authUser);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(LocalDateTime.now().plusMinutes(refreshTokenExpirationMinutes));
        refreshToken.setRevoked(false);

        return refreshTokenRepository.save(refreshToken);
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public Optional<RefreshToken> findActiveByToken(String token) {
        return refreshTokenRepository.findByTokenAndRevokedFalse(token);
    }

    @Override
    @Transactional(readOnly = true)
    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.isRevoked()) {
            throw new RuntimeException("Refresh token revoked. Please login again.");
        }

        if (token.getExpiryDate().isBefore(LocalDateTime.now())) {
            token.setRevoked(true);
            refreshTokenRepository.save(token);
            throw new RuntimeException("Refresh token expired. Please login again.");
        }

        return token;
    }

    @Override
    @Transactional
    public void deleteByToken(RefreshToken token) {
        token.setRevoked(true);
        refreshTokenRepository.save(token);
    }

    @Override
    @Transactional
    public void deleteByAuthUser(AuthUser authUser) {
        refreshTokenRepository.revokeByUserId(authUser.getId());
    }

    @Override
    @Transactional
    public void deleteByUserId(Long userId) {
        if (!authUserRepository.existsById(userId)) {
            return;
        }
        refreshTokenRepository.revokeByUserId(userId);
    }
}
