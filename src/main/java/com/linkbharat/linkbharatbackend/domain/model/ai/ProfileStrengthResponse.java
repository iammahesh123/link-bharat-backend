package com.linkbharat.linkbharatbackend.domain.model.ai;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfileStrengthResponse {
    /** 0–100 completeness score. */
    private int score;
    /** Short label: Beginner / Intermediate / All-Star */
    private String level;
    /** Bullet-point suggestions to improve the profile. */
    private List<String> suggestions;
}
