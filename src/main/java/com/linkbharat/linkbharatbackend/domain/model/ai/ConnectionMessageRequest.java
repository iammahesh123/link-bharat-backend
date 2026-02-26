package com.linkbharat.linkbharatbackend.domain.model.ai;

import lombok.Data;

@Data
public class ConnectionMessageRequest {
    private String myName;
    private String myHeadline;
    private String recipientName;
    private String recipientHeadline;
}
