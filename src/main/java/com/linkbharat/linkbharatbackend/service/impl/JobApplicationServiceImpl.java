package com.linkbharat.linkbharatbackend.service.impl;

import com.linkbharat.linkbharatbackend.domain.entity.*;
import com.linkbharat.linkbharatbackend.domain.enums.ApplicationStatus;
import com.linkbharat.linkbharatbackend.domain.enums.JobStatus;
import com.linkbharat.linkbharatbackend.domain.model.*;
import com.linkbharat.linkbharatbackend.repository.*;
import com.linkbharat.linkbharatbackend.service.JobApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class JobApplicationServiceImpl implements JobApplicationService {

    private final JobApplicationRepository jobApplicationRepository;
    private final JobRepository jobRepository;
    private final CompanyAdminRepository companyAdminRepository;
    private final AuthUserRepository authUserRepository;
    private final UserProfileRepository userProfileRepository;

    @Override
    @Transactional
    public JobApplicationResponse applyToJob(JobApplicationRequest request, String username) {
        AuthUser user = findUserByUsername(username);
        Job job = jobRepository.findById(request.getJobId())
                .orElseThrow(() -> new RuntimeException("Job not found"));

        if (job.getStatus() != JobStatus.ACTIVE) {
            throw new RuntimeException("This job is no longer accepting applications");
        }

        if (jobApplicationRepository.existsByJobAndApplicant(job, user)) {
            throw new RuntimeException("You have already applied to this job");
        }

        JobApplication application = new JobApplication();
        application.setJob(job);
        application.setApplicant(user);
        application.setStatus(ApplicationStatus.APPLIED);
        application.setCoverLetter(request.getCoverLetter());
        application.setResumeUrl(request.getResumeUrl());

        JobApplication saved = jobApplicationRepository.save(application);

        // Update applicant count
        job.setApplicantCount(job.getApplicantCount() + 1);
        jobRepository.save(job);

        return mapToResponse(saved);
    }

    @Override
    public Page<JobApplicationResponse> getMyApplications(int page, int size, String username) {
        AuthUser user = findUserByUsername(username);
        Pageable pageable = PageRequest.of(page, size);
        return jobApplicationRepository.findByApplicantOrderByCreatedAtDesc(user, pageable)
                .map(this::mapToResponse);
    }

    @Override
    public Page<JobApplicationResponse> getApplicationsByJob(Long jobId, ApplicationStatus status, int page, int size, String username) {
        AuthUser user = findUserByUsername(username);
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        // Verify recruiter has access
        if (!companyAdminRepository.existsByCompanyAndUser(job.getCompany(), user)) {
            throw new RuntimeException("You are not authorized to view applications for this job");
        }

        Pageable pageable = PageRequest.of(page, size);
        if (status != null) {
            return jobApplicationRepository.findByJobAndStatusOrderByCreatedAtDesc(job, status, pageable)
                    .map(this::mapToResponse);
        }
        return jobApplicationRepository.findByJobOrderByCreatedAtDesc(job, pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional
    public JobApplicationResponse updateApplicationStatus(Long applicationId, ApplicationStatusUpdateRequest request, String username) {
        AuthUser user = findUserByUsername(username);
        JobApplication application = jobApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        // Verify recruiter access
        if (!companyAdminRepository.existsByCompanyAndUser(application.getJob().getCompany(), user)) {
            throw new RuntimeException("You are not authorized to update this application");
        }

        application.setStatus(request.getStatus());
        if (request.getRecruiterNotes() != null) {
            application.setRecruiterNotes(request.getRecruiterNotes());
        }

        JobApplication saved = jobApplicationRepository.save(application);
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public void withdrawApplication(Long applicationId, String username) {
        AuthUser user = findUserByUsername(username);
        JobApplication application = jobApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        if (!application.getApplicant().getId().equals(user.getId())) {
            throw new RuntimeException("You can only withdraw your own applications");
        }

        application.setStatus(ApplicationStatus.WITHDRAWN);
        jobApplicationRepository.save(application);

        // Decrement applicant count
        Job job = application.getJob();
        job.setApplicantCount(Math.max(0, job.getApplicantCount() - 1));
        jobRepository.save(job);
    }

    private JobApplicationResponse mapToResponse(JobApplication application) {
        UserSummaryResponse applicantSummary = UserSummaryResponse.builder()
                .id(application.getApplicant().getId())
                .username(application.getApplicant().getUsername())
                .email(application.getApplicant().getEmail())
                .build();

        // Try to enrich with profile data
        try {
            UserProfile profile = userProfileRepository.findByAuthUser(application.getApplicant()).orElse(null);
            if (profile != null) {
                applicantSummary.setAvatarUrl(profile.getAvatarUrl());
                applicantSummary.setHeadline(profile.getHeadLine());
                applicantSummary.setLocation(profile.getLocation());
            }
        } catch (Exception ignored) {}

        return JobApplicationResponse.builder()
                .applicationId(application.getApplicationId())
                .jobId(application.getJob().getJobId())
                .jobTitle(application.getJob().getTitle())
                .companyName(application.getJob().getCompany().getName())
                .companyLogoUrl(application.getJob().getCompany().getLogoUrl())
                .applicant(applicantSummary)
                .status(application.getStatus())
                .coverLetter(application.getCoverLetter())
                .resumeUrl(application.getResumeUrl())
                .recruiterNotes(application.getRecruiterNotes())
                .createdAt(application.getCreatedAt())
                .updatedAt(application.getUpdatedAt())
                .build();
    }

    private AuthUser findUserByUsername(String username) {
        return authUserRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
