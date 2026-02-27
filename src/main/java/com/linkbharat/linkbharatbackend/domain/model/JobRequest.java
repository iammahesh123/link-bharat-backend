package com.linkbharat.linkbharatbackend.domain.model;

import com.linkbharat.linkbharatbackend.domain.enums.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
public class JobRequest {
    @NotBlank(message = "Job title is required")
    private String title;

    @NotBlank(message = "Job description is required")
    private String description;

    private String requirements;
    private String responsibilities;

    @NotNull(message = "Company ID is required")
    private Long companyId;

    private EmploymentType employmentType;
    private LocationType locationType;
    private String location;
    private Long salaryMin;
    private Long salaryMax;
    private String currency = "INR";
    private ExperienceLevel experienceLevel;
    private Set<String> skills;
    private JobStatus status = JobStatus.DRAFT;
    private LocalDate applicationDeadline;
}
