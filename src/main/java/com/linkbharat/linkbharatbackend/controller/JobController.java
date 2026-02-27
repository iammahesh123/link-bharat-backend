package com.linkbharat.linkbharatbackend.controller;

import com.linkbharat.linkbharatbackend.domain.enums.*;
import com.linkbharat.linkbharatbackend.domain.model.JobRequest;
import com.linkbharat.linkbharatbackend.domain.model.JobResponse;
import com.linkbharat.linkbharatbackend.service.JobService;
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

import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
@Tag(name = "Jobs", description = "APIs for creating, searching, and managing job postings.")
@SecurityRequirement(name = "bearerAuth")
public class JobController {

    private final JobService jobService;

    @Operation(summary = "Create a new job posting")
    @PostMapping
    public ResponseEntity<JobResponse> createJob(
            @Valid @RequestBody JobRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(jobService.createJob(request, userDetails.getUsername()));
    }

    @Operation(summary = "Search and filter jobs")
    @GetMapping
    public ResponseEntity<Page<JobResponse>> searchJobs(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) EmploymentType employmentType,
            @RequestParam(required = false) ExperienceLevel experienceLevel,
            @RequestParam(required = false) LocationType locationType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(jobService.searchJobs(keyword, location, employmentType,
                experienceLevel, locationType, page, size,
                userDetails != null ? userDetails.getUsername() : null));
    }

    @Operation(summary = "Get job by ID")
    @GetMapping("/{jobId}")
    public ResponseEntity<JobResponse> getJobById(
            @PathVariable Long jobId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(jobService.getJobById(jobId,
                userDetails != null ? userDetails.getUsername() : null));
    }

    @Operation(summary = "Update a job posting")
    @PutMapping("/{jobId}")
    public ResponseEntity<JobResponse> updateJob(
            @PathVariable Long jobId,
            @Valid @RequestBody JobRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(jobService.updateJob(jobId, request, userDetails.getUsername()));
    }

    @Operation(summary = "Delete a job posting (soft delete)")
    @DeleteMapping("/{jobId}")
    public ResponseEntity<Void> deleteJob(
            @PathVariable Long jobId,
            @AuthenticationPrincipal UserDetails userDetails) {
        jobService.deleteJob(jobId, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Close a job posting")
    @PutMapping("/{jobId}/close")
    public ResponseEntity<Void> closeJob(
            @PathVariable Long jobId,
            @AuthenticationPrincipal UserDetails userDetails) {
        jobService.closeJob(jobId, userDetails.getUsername());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get jobs posted by the current recruiter")
    @GetMapping("/my")
    public ResponseEntity<Page<JobResponse>> getMyPostedJobs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(jobService.getMyPostedJobs(page, size, userDetails.getUsername()));
    }

    @Operation(summary = "Get jobs by company")
    @GetMapping("/company/{companyId}")
    public ResponseEntity<Page<JobResponse>> getJobsByCompany(
            @PathVariable Long companyId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(jobService.getJobsByCompany(companyId, page, size,
                userDetails != null ? userDetails.getUsername() : null));
    }

    @Operation(summary = "Toggle save/unsave a job")
    @PostMapping("/{jobId}/save")
    public ResponseEntity<Map<String, Object>> toggleSaveJob(
            @PathVariable Long jobId,
            @AuthenticationPrincipal UserDetails userDetails) {
        boolean saved = jobService.toggleSaveJob(jobId, userDetails.getUsername());
        return ResponseEntity.ok(Map.of("saved", saved));
    }

    @Operation(summary = "Get saved/bookmarked jobs")
    @GetMapping("/saved")
    public ResponseEntity<Page<JobResponse>> getSavedJobs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(jobService.getSavedJobs(page, size, userDetails.getUsername()));
    }

    @Operation(summary = "Get recommended jobs based on user skills")
    @GetMapping("/recommended")
    public ResponseEntity<Page<JobResponse>> getRecommendedJobs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(jobService.getRecommendedJobs(page, size, userDetails.getUsername()));
    }
}
