package com.linkbharat.linkbharatbackend.domain.model;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class UserProfileResponse {
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
}
