package com.linkbharat.linkbharatbackend.domain.enums;

import lombok.Getter;

@Getter
public enum LocationType {
    REMOTE("Remote"),
    ONSITE("On-site"),
    HYBRID("Hybrid");

    private final String displayName;

    LocationType(String displayName) {
        this.displayName = displayName;
    }
}
