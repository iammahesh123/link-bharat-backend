package com.linkbharat.linkbharatbackend.service.impl;

import com.linkbharat.linkbharatbackend.domain.entity.*;
import com.linkbharat.linkbharatbackend.domain.enums.*;
import com.linkbharat.linkbharatbackend.domain.model.JobRequest;
import com.linkbharat.linkbharatbackend.domain.model.JobResponse;
import com.linkbharat.linkbharatbackend.domain.model.UserSummaryResponse;
import com.linkbharat.linkbharatbackend.repository.*;
import com.linkbharat.linkbharatbackend.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobServiceImpl implements JobService {

    private final JobRepository jobRepository;
    private final CompanyRepository companyRepository;
    private final CompanyAdminRepository companyAdminRepository;
    private final JobApplicationRepository jobApplicationRepository;
    private final SavedJobRepository savedJobRepository;
    private final AuthUserRepository authUserRepository;
    private final UserProfileRepository userProfileRepository;

    @Override
    @Transactional
    public JobResponse createJob(JobRequest request, String username) {
        AuthUser user = findUserByUsername(username);
        Company company = companyRepository.findById(request.getCompanyId())
                .orElseThrow(() -> new RuntimeException("Company not found"));

        if (!companyAdminRepository.existsByCompanyAndUser(company, user)) {
            throw new RuntimeException("You are not authorized to post jobs for this company");
        }

        Job job = new Job();
        job.setTitle(request.getTitle());
        job.setDescription(request.getDescription());
        job.setRequirements(request.getRequirements());
        job.setResponsibilities(request.getResponsibilities());
        job.setCompany(company);
        job.setPostedBy(user);
        job.setEmploymentType(request.getEmploymentType());
        job.setLocationType(request.getLocationType());
        job.setLocation(request.getLocation());
        job.setSalaryMin(request.getSalaryMin());
        job.setSalaryMax(request.getSalaryMax());
        job.setCurrency(request.getCurrency());
        job.setExperienceLevel(request.getExperienceLevel());
        job.setSkills(request.getSkills() != null ? request.getSkills() : new HashSet<>());
        job.setStatus(request.getStatus() != null ? request.getStatus() : JobStatus.DRAFT);
        job.setApplicationDeadline(request.getApplicationDeadline());

        Job saved = jobRepository.save(job);
        return mapToResponse(saved, username);
    }

    @Override
    @Transactional
    public JobResponse updateJob(Long jobId, JobRequest request, String username) {
        AuthUser user = findUserByUsername(username);
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        if (!job.getPostedBy().getId().equals(user.getId())) {
            throw new RuntimeException("You are not authorized to update this job");
        }

        if (request.getTitle() != null) job.setTitle(request.getTitle());
        if (request.getDescription() != null) job.setDescription(request.getDescription());
        if (request.getRequirements() != null) job.setRequirements(request.getRequirements());
        if (request.getResponsibilities() != null) job.setResponsibilities(request.getResponsibilities());
        if (request.getEmploymentType() != null) job.setEmploymentType(request.getEmploymentType());
        if (request.getLocationType() != null) job.setLocationType(request.getLocationType());
        if (request.getLocation() != null) job.setLocation(request.getLocation());
        if (request.getSalaryMin() != null) job.setSalaryMin(request.getSalaryMin());
        if (request.getSalaryMax() != null) job.setSalaryMax(request.getSalaryMax());
        if (request.getCurrency() != null) job.setCurrency(request.getCurrency());
        if (request.getExperienceLevel() != null) job.setExperienceLevel(request.getExperienceLevel());
        if (request.getSkills() != null) job.setSkills(request.getSkills());
        if (request.getStatus() != null) job.setStatus(request.getStatus());
        if (request.getApplicationDeadline() != null) job.setApplicationDeadline(request.getApplicationDeadline());

        Job saved = jobRepository.save(job);
        return mapToResponse(saved, username);
    }

    @Override
    public JobResponse getJobById(Long jobId, String username) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));
        // Increment view count
        job.setViewCount(job.getViewCount() + 1);
        jobRepository.save(job);
        return mapToResponse(job, username);
    }

    @Override
    public Page<JobResponse> searchJobs(String keyword, String location, EmploymentType employmentType,
                                         ExperienceLevel experienceLevel, LocationType locationType,
                                         int page, int size, String username) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Job> jobs = jobRepository.searchWithFilters(keyword, location, employmentType, experienceLevel, locationType, pageable);
        return jobs.map(j -> mapToResponse(j, username));
    }

    @Override
    public Page<JobResponse> getJobsByCompany(Long companyId, int page, int size, String username) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found"));
        Pageable pageable = PageRequest.of(page, size);
        Page<Job> jobs = jobRepository.findByCompanyAndStatusAndIsDeletedFalseOrderByCreatedAtDesc(company, JobStatus.ACTIVE, pageable);
        return jobs.map(j -> mapToResponse(j, username));
    }

    @Override
    public Page<JobResponse> getMyPostedJobs(int page, int size, String username) {
        AuthUser user = findUserByUsername(username);
        Pageable pageable = PageRequest.of(page, size);
        Page<Job> jobs = jobRepository.findByPostedByAndIsDeletedFalseOrderByCreatedAtDesc(user, pageable);
        return jobs.map(j -> mapToResponse(j, username));
    }

    @Override
    @Transactional
    public void closeJob(Long jobId, String username) {
        AuthUser user = findUserByUsername(username);
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));
        if (!job.getPostedBy().getId().equals(user.getId())) {
            throw new RuntimeException("Not authorized");
        }
        job.setStatus(JobStatus.CLOSED);
        jobRepository.save(job);
    }

    @Override
    @Transactional
    public void deleteJob(Long jobId, String username) {
        AuthUser user = findUserByUsername(username);
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));
        if (!job.getPostedBy().getId().equals(user.getId())) {
            throw new RuntimeException("Not authorized");
        }
        job.setDeleted(true);
        jobRepository.save(job);
    }

    @Override
    @Transactional
    public boolean toggleSaveJob(Long jobId, String username) {
        AuthUser user = findUserByUsername(username);
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        var existing = savedJobRepository.findByJobAndUser(job, user);
        if (existing.isPresent()) {
            savedJobRepository.delete(existing.get());
            return false;
        } else {
            SavedJob savedJob = new SavedJob();
            savedJob.setJob(job);
            savedJob.setUser(user);
            savedJobRepository.save(savedJob);
            return true;
        }
    }

    @Override
    public Page<JobResponse> getSavedJobs(int page, int size, String username) {
        AuthUser user = findUserByUsername(username);
        Pageable pageable = PageRequest.of(page, size);
        Page<SavedJob> savedJobs = savedJobRepository.findByUserOrderByCreatedAtDesc(user, pageable);
        return savedJobs.map(sj -> mapToResponse(sj.getJob(), username));
    }

    @Override
    public Page<JobResponse> getRecommendedJobs(int page, int size, String username) {
        AuthUser user = findUserByUsername(username);
        Pageable pageable = PageRequest.of(page, size);

        // Get user skills from profile
        try {
            UserProfile profile = userProfileRepository.findByAuthUser(user)
                    .orElse(null);
            if (profile != null && profile.getSkills() != null && !profile.getSkills().isEmpty()) {
                Set<String> lowerSkills = profile.getSkills().stream()
                        .map(String::toLowerCase)
                        .collect(Collectors.toSet());
                return jobRepository.findBySkillsIn(lowerSkills, pageable)
                        .map(j -> mapToResponse(j, username));
            }
        } catch (Exception ignored) {}

        // Fallback: return active jobs
        return jobRepository.findByStatusAndIsDeletedFalseOrderByCreatedAtDesc(JobStatus.ACTIVE, pageable)
                .map(j -> mapToResponse(j, username));
    }

    private JobResponse mapToResponse(Job job, String username) {
        boolean hasApplied = false;
        boolean isSaved = false;

        if (username != null) {
            try {
                AuthUser user = findUserByUsername(username);
                hasApplied = jobApplicationRepository.existsByJobAndApplicant(job, user);
                isSaved = savedJobRepository.existsByJobAndUser(job, user);
            } catch (Exception ignored) {}
        }

        UserSummaryResponse postedBy = UserSummaryResponse.builder()
                .id(job.getPostedBy().getId())
                .username(job.getPostedBy().getUsername())
                .email(job.getPostedBy().getEmail())
                .build();

        return JobResponse.builder()
                .jobId(job.getJobId())
                .title(job.getTitle())
                .description(job.getDescription())
                .requirements(job.getRequirements())
                .responsibilities(job.getResponsibilities())
                .companyId(job.getCompany().getCompanyId())
                .companyName(job.getCompany().getName())
                .companyLogoUrl(job.getCompany().getLogoUrl())
                .postedBy(postedBy)
                .employmentType(job.getEmploymentType())
                .locationType(job.getLocationType())
                .location(job.getLocation())
                .salaryMin(job.getSalaryMin())
                .salaryMax(job.getSalaryMax())
                .currency(job.getCurrency())
                .experienceLevel(job.getExperienceLevel())
                .skills(job.getSkills())
                .status(job.getStatus())
                .applicationDeadline(job.getApplicationDeadline())
                .viewCount(job.getViewCount())
                .applicantCount(job.getApplicantCount())
                .hasApplied(hasApplied)
                .isSaved(isSaved)
                .createdAt(job.getCreatedAt())
                .updatedAt(job.getUpdatedAt())
                .build();
    }

    private AuthUser findUserByUsername(String username) {
        return authUserRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
