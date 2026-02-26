package com.linkbharat.linkbharatbackend.domain.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class UserProfileResponse {
    /** The AuthUser.id — used by the frontend to build profile URLs like /profile/{id} */
    private Long id;
    private Long userProfileId;
    private String name;
    private String headLine;
    private String avatarUrl;
    private String coverageImageUrl;
    private String location;
    private int connections;
    private String about;
    private Set<String> skills = new HashSet<>();
    private boolean isConnected;
    private boolean isPending;
    private List<ExperienceResponse> experiences = new ArrayList<>();
    private List<EducationResponse> educations = new ArrayList<>();
    private boolean isOnboardingComplete;
}
