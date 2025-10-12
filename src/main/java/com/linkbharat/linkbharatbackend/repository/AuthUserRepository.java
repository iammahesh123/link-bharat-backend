package com.linkbharat.linkbharatbackend.repository;

import com.linkbharat.linkbharatbackend.domain.entity.AuthUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthUserRepository extends JpaRepository<AuthUser, Long> {


    Optional<AuthUser> findByEmail(String email);
    Optional<AuthUser> findByUsername(String username);

    @Query("SELECT u FROM AuthUser u WHERE u.username = :username OR u.email = :email")
    Optional<AuthUser> findByUsernameOrEmail(@Param("username") String username, @Param("email") String email);

    Boolean existsByEmail(String email);
    Boolean existsByUsername(String username);
}
