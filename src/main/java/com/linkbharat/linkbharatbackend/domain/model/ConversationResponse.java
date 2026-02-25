package com.linkbharat.linkbharatbackend.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConversationResponse {
    private Long conversationId;
    private UserSummaryResponse participant;
    private String lastMessage;
    private int unreadCount;
    private LocalDateTime updatedAt;
}
