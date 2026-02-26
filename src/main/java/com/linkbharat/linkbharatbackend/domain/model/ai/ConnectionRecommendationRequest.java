package com.linkbharat.linkbharatbackend.domain.model.ai;

import lombok.Data;

@Data
public class ConnectionRecommendationRequest {
    /** Current user's name + headline + skills (comma-separated). */
    private String currentUserName;
    private String currentUserHeadline;
    private String currentUserSkills;

    /** Suggested person's name + headline. */
    private String suggestedUserName;
    private String suggestedUserHeadline;
}
