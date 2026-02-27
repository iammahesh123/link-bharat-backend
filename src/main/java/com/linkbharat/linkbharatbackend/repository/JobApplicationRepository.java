package com.linkbharat.linkbharatbackend.repository;

import com.linkbharat.linkbharatbackend.domain.entity.AuthUser;
import com.linkbharat.linkbharatbackend.domain.entity.Job;
import com.linkbharat.linkbharatbackend.domain.entity.JobApplication;
import com.linkbharat.linkbharatbackend.domain.enums.ApplicationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {
    Page<JobApplication> findByJobOrderByCreatedAtDesc(Job job, Pageable pageable);
    Page<JobApplication> findByApplicantOrderByCreatedAtDesc(AuthUser applicant, Pageable pageable);
    Optional<JobApplication> findByJobAndApplicant(Job job, AuthUser applicant);
    boolean existsByJobAndApplicant(Job job, AuthUser applicant);
    long countByJob(Job job);
    long countByJobAndStatus(Job job, ApplicationStatus status);
    Page<JobApplication> findByJobAndStatusOrderByCreatedAtDesc(Job job, ApplicationStatus status, Pageable pageable);
}
