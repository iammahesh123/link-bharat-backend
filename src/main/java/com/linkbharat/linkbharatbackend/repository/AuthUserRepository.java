package com.linkbharat.linkbharatbackend.repository;

import com.linkbharat.linkbharatbackend.domain.entity.AuthUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuthUserRepository extends JpaRepository<AuthUser, Long> {

    Optional<AuthUser> findByEmail(String email);
    Optional<AuthUser> findByUsername(String username);

    @Query("SELECT u FROM AuthUser u WHERE u.username = :username OR u.email = :email")
    Optional<AuthUser> findByUsernameOrEmail(@Param("username") String username, @Param("email") String email);

    Boolean existsByEmail(String email);
    Boolean existsByUsername(String username);

    // Full-text search by username or email (case-insensitive)
    @Query("SELECT u FROM AuthUser u WHERE LOWER(u.username) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(u.email) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<AuthUser> searchByUsernameOrEmail(@Param("query") String query, Pageable pageable);
}
