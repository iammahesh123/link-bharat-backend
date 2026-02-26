package com.linkbharat.linkbharatbackend.domain.model.ai;

import lombok.Data;
import java.util.List;

@Data
public class AutoCompleteRequest {
    /** Partial text the user has typed so far. */
    private String partial;
    /** Last few messages for context. */
    private List<SmartReplyRequest.MessageContext> context;
}
