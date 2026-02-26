package com.linkbharat.linkbharatbackend.domain.model.ai;

import lombok.Data;

@Data
public class RewriteRequest {
    /** The original draft message. */
    private String draft;
    /** One of: professional, friendly, concise */
    private String tone;
}
