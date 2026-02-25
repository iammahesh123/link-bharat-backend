package com.linkbharat.linkbharatbackend.domain.model;

import com.linkbharat.linkbharatbackend.domain.enums.ConnectionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConnectionResponse {
    private Long connectionId;
    private UserSummaryResponse requester;
    private UserSummaryResponse receiver;
    private ConnectionStatus status;
    private LocalDateTime createdAt;
    // The "other" user from the perspective of the current user
    private UserSummaryResponse otherUser;
}
