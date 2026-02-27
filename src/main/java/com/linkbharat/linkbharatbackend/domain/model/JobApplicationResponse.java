package com.linkbharat.linkbharatbackend.domain.model;

import com.linkbharat.linkbharatbackend.domain.enums.ApplicationStatus;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JobApplicationResponse {
    private Long applicationId;
    private Long jobId;
    private String jobTitle;
    private String companyName;
    private String companyLogoUrl;
    private UserSummaryResponse applicant;
    private ApplicationStatus status;
    private String coverLetter;
    private String resumeUrl;
    private String recruiterNotes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
