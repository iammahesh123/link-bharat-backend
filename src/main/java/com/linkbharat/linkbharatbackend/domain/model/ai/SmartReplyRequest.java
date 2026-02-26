package com.linkbharat.linkbharatbackend.domain.model.ai;

import lombok.Data;
import java.util.List;

@Data
public class SmartReplyRequest {
    /** The last few messages in the conversation for context (max 10). */
    private List<MessageContext> messages;

    @Data
    public static class MessageContext {
        private String senderName;
        private String content;
        private boolean isMe;
    }
}
