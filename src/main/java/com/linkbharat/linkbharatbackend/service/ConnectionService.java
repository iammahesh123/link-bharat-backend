package com.linkbharat.linkbharatbackend.service;

import com.linkbharat.linkbharatbackend.domain.model.ConnectionResponse;
import com.linkbharat.linkbharatbackend.domain.model.UserSummaryResponse;

import java.util.List;

public interface ConnectionService {
    ConnectionResponse sendConnectionRequest(Long receiverId, String username);
    ConnectionResponse acceptConnectionRequest(Long connectionId, String username);
    ConnectionResponse ignoreConnectionRequest(Long connectionId, String username);
    void removeConnection(Long userId, String username);
    List<ConnectionResponse> getConnections(String username);
    List<ConnectionResponse> getPendingInvitations(String username);
    List<UserSummaryResponse> getSuggestions(String username, int page, int size);
    String getConnectionStatus(Long targetUserId, String username);
}
