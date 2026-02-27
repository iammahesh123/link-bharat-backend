package com.linkbharat.linkbharatbackend.service;

import com.linkbharat.linkbharatbackend.domain.enums.*;
import com.linkbharat.linkbharatbackend.domain.model.JobRequest;
import com.linkbharat.linkbharatbackend.domain.model.JobResponse;
import org.springframework.data.domain.Page;

public interface JobService {
    JobResponse createJob(JobRequest request, String username);
    JobResponse updateJob(Long jobId, JobRequest request, String username);
    JobResponse getJobById(Long jobId, String username);
    Page<JobResponse> searchJobs(String keyword, String location, EmploymentType employmentType,
                                  ExperienceLevel experienceLevel, LocationType locationType,
                                  int page, int size, String username);
    Page<JobResponse> getJobsByCompany(Long companyId, int page, int size, String username);
    Page<JobResponse> getMyPostedJobs(int page, int size, String username);
    void closeJob(Long jobId, String username);
    void deleteJob(Long jobId, String username);
    boolean toggleSaveJob(Long jobId, String username);
    Page<JobResponse> getSavedJobs(int page, int size, String username);
    Page<JobResponse> getRecommendedJobs(int page, int size, String username);
}
