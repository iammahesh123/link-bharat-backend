package com.linkbharat.linkbharatbackend.service;

import com.linkbharat.linkbharatbackend.domain.model.ConversationResponse;
import com.linkbharat.linkbharatbackend.domain.model.MessageRequest;
import com.linkbharat.linkbharatbackend.domain.model.MessageResponse;

import java.util.List;

public interface ConversationService {
    ConversationResponse getOrCreateConversation(Long otherUserId, String username);
    List<ConversationResponse> getConversations(String username);
    List<MessageResponse> getMessages(Long conversationId, String username);
    MessageResponse sendMessage(Long conversationId, MessageRequest request, String username);
    void markMessagesAsRead(Long conversationId, String username);
}
