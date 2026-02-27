package com.linkbharat.linkbharatbackend.domain.model;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CompanyResponse {
    private Long companyId;
    private String name;
    private String description;
    private String industry;
    private String website;
    private String logoUrl;
    private String coverImageUrl;
    private String location;
    private String employeeCount;
    private Integer foundedYear;
    private int followerCount;
    private boolean isVerified;
    private boolean isFollowing;
    private long jobCount;
    private UserSummaryResponse createdBy;
    private LocalDateTime createdAt;
}
