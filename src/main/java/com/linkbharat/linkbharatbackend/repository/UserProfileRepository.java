package com.linkbharat.linkbharatbackend.repository;

import com.linkbharat.linkbharatbackend.domain.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
}
