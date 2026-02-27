package com.linkbharat.linkbharatbackend.repository;

import com.linkbharat.linkbharatbackend.domain.entity.AuthUser;
import com.linkbharat.linkbharatbackend.domain.entity.Job;
import com.linkbharat.linkbharatbackend.domain.entity.SavedJob;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SavedJobRepository extends JpaRepository<SavedJob, Long> {
    Optional<SavedJob> findByJobAndUser(Job job, AuthUser user);
    boolean existsByJobAndUser(Job job, AuthUser user);
    Page<SavedJob> findByUserOrderByCreatedAtDesc(AuthUser user, Pageable pageable);
}
