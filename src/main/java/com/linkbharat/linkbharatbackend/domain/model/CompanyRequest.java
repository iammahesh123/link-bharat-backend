package com.linkbharat.linkbharatbackend.domain.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CompanyRequest {
    @NotBlank(message = "Company name is required")
    private String name;
    private String description;
    private String industry;
    private String website;
    private String logoUrl;
    private String coverImageUrl;
    private String location;
    private String employeeCount;
    private Integer foundedYear;
}
