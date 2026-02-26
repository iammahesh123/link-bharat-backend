package com.linkbharat.linkbharatbackend.domain.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EducationResponse {
    private Long educationId;
    private String institutionName;
    private String degree;
    private String fieldOfStudy;
    private String startYear;
    private String endYear;
    private String grade;
    private String description;
}
