package com.linkbharat.linkbharatbackend.service;

import com.linkbharat.linkbharatbackend.domain.entity.RefreshToken;
import com.linkbharat.linkbharatbackend.domain.entity.AuthUser;
import com.linkbharat.linkbharatbackend.repository.RefreshTokenRepository;
import com.linkbharat.linkbharatbackend.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${jwt.refresh-expiration}")
    private Long refreshTokenDurationMs;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, JwtTokenProvider jwtTokenProvider) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Transactional
    public RefreshToken createRefreshToken(AuthUser authUser) {
        // Delete existing refresh tokens for this user
        refreshTokenRepository.deleteByAuthUser(authUser);

        // Generate refresh token using JWT
        String refreshTokenString = jwtTokenProvider.generateRefreshToken(authUser);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setAuthUser(authUser);
        refreshToken.setToken(refreshTokenString);
        refreshToken.setExpiryDate(LocalDateTime.now().plusNanos(refreshTokenDurationMs));
        refreshToken.setRevoked(false);

        return refreshTokenRepository.save(refreshToken);
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException("Refresh token was expired. Please make a new signin request");
        }
        return token;
    }

    @Transactional
    public void deleteByUserId(Long userId) {
        // Implementation depends on your relationship setup
    }

    @Transactional
    public void deleteByAuthUser(AuthUser authUser) {
        refreshTokenRepository.deleteByAuthUser(authUser);
    }

    @Transactional
    public void deleteByToken(RefreshToken token) {
        refreshTokenRepository.delete(token);
    }
}