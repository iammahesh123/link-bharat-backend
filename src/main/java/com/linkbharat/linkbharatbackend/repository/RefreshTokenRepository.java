package com.linkbharat.linkbharatbackend.repository;

import com.linkbharat.linkbharatbackend.domain.entity.RefreshToken;
import com.linkbharat.linkbharatbackend.domain.entity.AuthUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);

    Optional<RefreshToken> findByAuthUser(AuthUser authUser);

    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.authUser = :authUser")
    void deleteByAuthUser(AuthUser authUser);

    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.expiryDate < CURRENT_TIMESTAMP")
    void deleteExpiredTokens();
}