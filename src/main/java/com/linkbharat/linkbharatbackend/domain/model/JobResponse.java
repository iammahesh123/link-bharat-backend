package com.linkbharat.linkbharatbackend.domain.model;

import com.linkbharat.linkbharatbackend.domain.enums.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JobResponse {
    private Long jobId;
    private String title;
    private String description;
    private String requirements;
    private String responsibilities;
    private Long companyId;
    private String companyName;
    private String companyLogoUrl;
    private UserSummaryResponse postedBy;
    private EmploymentType employmentType;
    private LocationType locationType;
    private String location;
    private Long salaryMin;
    private Long salaryMax;
    private String currency;
    private ExperienceLevel experienceLevel;
    private Set<String> skills;
    private JobStatus status;
    private LocalDate applicationDeadline;
    private int viewCount;
    private int applicantCount;
    private boolean hasApplied;
    private boolean isSaved;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
