package com.linkbharat.linkbharatbackend.domain.model;

import com.linkbharat.linkbharatbackend.domain.enums.ApplicationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ApplicationStatusUpdateRequest {
    @NotNull(message = "Status is required")
    private ApplicationStatus status;
    private String recruiterNotes;
}
