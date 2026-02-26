package com.linkbharat.linkbharatbackend.domain.model.ai;

import lombok.Data;
import java.util.List;

@Data
public class SummaryRequest {
    /** Full list of messages to summarize. */
    private List<SmartReplyRequest.MessageContext> messages;
}
