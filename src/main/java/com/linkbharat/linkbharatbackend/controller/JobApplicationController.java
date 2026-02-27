package com.linkbharat.linkbharatbackend.controller;

import com.linkbharat.linkbharatbackend.domain.enums.ApplicationStatus;
import com.linkbharat.linkbharatbackend.domain.model.ApplicationStatusUpdateRequest;
import com.linkbharat.linkbharatbackend.domain.model.JobApplicationRequest;
import com.linkbharat.linkbharatbackend.domain.model.JobApplicationResponse;
import com.linkbharat.linkbharatbackend.service.JobApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
@Tag(name = "Job Applications", description = "APIs for applying to jobs, managing applications, and processing applicants.")
@SecurityRequirement(name = "bearerAuth")
public class JobApplicationController {

    private final JobApplicationService jobApplicationService;

    @Operation(summary = "Apply to a job")
    @PostMapping
    public ResponseEntity<JobApplicationResponse> applyToJob(
            @Valid @RequestBody JobApplicationRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(jobApplicationService.applyToJob(request, userDetails.getUsername()));
    }

    @Operation(summary = "Get current user's job applications")
    @GetMapping("/my")
    public ResponseEntity<Page<JobApplicationResponse>> getMyApplications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(jobApplicationService.getMyApplications(page, size, userDetails.getUsername()));
    }

    @Operation(summary = "Get applications for a specific job (recruiter only)")
    @GetMapping("/job/{jobId}")
    public ResponseEntity<Page<JobApplicationResponse>> getApplicationsByJob(
            @PathVariable Long jobId,
            @RequestParam(required = false) ApplicationStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(jobApplicationService.getApplicationsByJob(jobId, status, page, size, userDetails.getUsername()));
    }

    @Operation(summary = "Update application status (recruiter only)")
    @PutMapping("/{applicationId}/status")
    public ResponseEntity<JobApplicationResponse> updateApplicationStatus(
            @PathVariable Long applicationId,
            @Valid @RequestBody ApplicationStatusUpdateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(jobApplicationService.updateApplicationStatus(applicationId, request, userDetails.getUsername()));
    }

    @Operation(summary = "Withdraw a job application")
    @DeleteMapping("/{applicationId}")
    public ResponseEntity<Void> withdrawApplication(
            @PathVariable Long applicationId,
            @AuthenticationPrincipal UserDetails userDetails) {
        jobApplicationService.withdrawApplication(applicationId, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
}
