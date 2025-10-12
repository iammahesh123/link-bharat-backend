package com.linkbharat.linkbharatbackend.domain.enums;

import lombok.Getter;

@Getter
public enum EmploymentType {
    FULL_TIME("Full Time"),
    PART_TIME("Part Time"),
    CONTRACT("Contract"),
    FREELANCE("Freelance"),
    SELF_EMPLOYED("Self Employed"),
    INTERN("Intern"),
    TRAINEE("Trainee"),
    INTERNSHIP("Internship"),
    EMPTY("");

    private final String displayName;

    EmploymentType(String displayName) {
        this.displayName = displayName;
    }
}
