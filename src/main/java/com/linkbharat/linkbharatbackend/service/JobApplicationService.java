package com.linkbharat.linkbharatbackend.service;

import com.linkbharat.linkbharatbackend.domain.enums.ApplicationStatus;
import com.linkbharat.linkbharatbackend.domain.model.ApplicationStatusUpdateRequest;
import com.linkbharat.linkbharatbackend.domain.model.JobApplicationRequest;
import com.linkbharat.linkbharatbackend.domain.model.JobApplicationResponse;
import org.springframework.data.domain.Page;

public interface JobApplicationService {
    JobApplicationResponse applyToJob(JobApplicationRequest request, String username);
    Page<JobApplicationResponse> getMyApplications(int page, int size, String username);
    Page<JobApplicationResponse> getApplicationsByJob(Long jobId, ApplicationStatus status, int page, int size, String username);
    JobApplicationResponse updateApplicationStatus(Long applicationId, ApplicationStatusUpdateRequest request, String username);
    void withdrawApplication(Long applicationId, String username);
}
