package com.linkbharat.linkbharatbackend.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageResponse {
    private Long messageId;
    private Long conversationId;
    private UserSummaryResponse sender;
    private String content;
    @JsonProperty("isRead")
    private boolean isRead;
    private LocalDateTime createdAt;
}
