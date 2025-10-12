package com.linkbharat.linkbharatbackend.domain.model;

import com.linkbharat.linkbharatbackend.domain.enums.EmploymentType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExperienceResponse {
    private Long experienceId;
    private String title;
    @Enumerated(EnumType.STRING)
    private EmploymentType employmentType;
    private String companyName;
    private LocalDate startDate;
    private LocalDate endDate;
    private String location;
    private String locationType;
    private String description;
    private Set<String> top5Skills = new HashSet<>();
    private String mediaUrl;
    private boolean isCurrentJob;
}
