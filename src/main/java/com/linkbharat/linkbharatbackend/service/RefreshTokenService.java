package com.linkbharat.linkbharatbackend.service;

import com.linkbharat.linkbharatbackend.domain.entity.AuthUser;
import com.linkbharat.linkbharatbackend.domain.entity.RefreshToken;

import java.util.Optional;

public interface RefreshTokenService {
    RefreshToken createRefreshToken(AuthUser authUser);
    RefreshToken verifyExpiration(RefreshToken token);
    void deleteByToken(RefreshToken token);
    void deleteByAuthUser(AuthUser authUser);
    void deleteByUserId(Long userId);
    Optional<RefreshToken> findByToken(String token);
    Optional<RefreshToken> findActiveByToken(String token);
}
