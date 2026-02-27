package com.linkbharat.linkbharatbackend.domain.enums;

import lombok.Getter;

@Getter
public enum ExperienceLevel {
    ENTRY("Entry Level"),
    MID("Mid Level"),
    SENIOR("Senior Level"),
    LEAD("Lead"),
    EXECUTIVE("Executive");

    private final String displayName;

    ExperienceLevel(String displayName) {
        this.displayName = displayName;
    }
}
