package com.linkbharat.linkbharatbackend.repository;

import com.linkbharat.linkbharatbackend.domain.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {

    /** Find a profile by the linked AuthUser's ID (AuthUser.id = the URL-facing user ID) */
    Optional<UserProfile> findByAuthUser_Id(Long authUserId);

    /** Find a profile by the linked AuthUser's username */
    Optional<UserProfile> findByAuthUser_Username(String username);

    /** Find a profile by the linked AuthUser's email */
    Optional<UserProfile> findByAuthUser_Email(String email);
}
