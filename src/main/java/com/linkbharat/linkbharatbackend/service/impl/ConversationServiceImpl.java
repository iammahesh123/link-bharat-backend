package com.linkbharat.linkbharatbackend.service.impl;

import com.linkbharat.linkbharatbackend.domain.entity.AuthUser;
import com.linkbharat.linkbharatbackend.domain.entity.Conversation;
import com.linkbharat.linkbharatbackend.domain.entity.Message;
import com.linkbharat.linkbharatbackend.domain.enums.NotificationType;
import com.linkbharat.linkbharatbackend.domain.model.ConversationResponse;
import com.linkbharat.linkbharatbackend.domain.model.MessageRequest;
import com.linkbharat.linkbharatbackend.domain.model.MessageResponse;
import com.linkbharat.linkbharatbackend.domain.model.UserSummaryResponse;
import com.linkbharat.linkbharatbackend.exceptions.ResourceNotFoundException;
import com.linkbharat.linkbharatbackend.repository.AuthUserRepository;
import com.linkbharat.linkbharatbackend.repository.ConversationRepository;
import com.linkbharat.linkbharatbackend.repository.MessageRepository;
import com.linkbharat.linkbharatbackend.service.ConversationService;
import com.linkbharat.linkbharatbackend.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConversationServiceImpl implements ConversationService {

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final AuthUserRepository authUserRepository;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public ConversationResponse getOrCreateConversation(Long otherUserId, String username) {
        AuthUser currentUser = findUserByUsername(username);
        AuthUser otherUser = authUserRepository.findById(otherUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + otherUserId));

        Conversation conversation = conversationRepository.findBetweenUsers(currentUser, otherUser)
                .orElseGet(() -> {
                    Conversation newConv = new Conversation();
                    newConv.setUserOne(currentUser);
                    newConv.setUserTwo(otherUser);
                    return conversationRepository.save(newConv);
                });

        return toConversationResponse(conversation, currentUser);
    }

    @Override
    public List<ConversationResponse> getConversations(String username) {
        AuthUser user = findUserByUsername(username);
        return conversationRepository.findByUserOrderByUpdatedAtDesc(user)
                .stream()
                .map(c -> toConversationResponse(c, user))
                .collect(Collectors.toList());
    }

    @Override
    public List<MessageResponse> getMessages(Long conversationId, String username) {
        AuthUser user = findUserByUsername(username);
        Conversation conversation = findConversationById(conversationId);

        if (!isParticipant(conversation, user)) {
            throw new RuntimeException("You are not a participant of this conversation");
        }

        return messageRepository.findByConversationOrderByCreatedAtAsc(conversation)
                .stream()
                .map(this::toMessageResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public MessageResponse sendMessage(Long conversationId, MessageRequest request, String username) {
        AuthUser sender = findUserByUsername(username);
        Conversation conversation = findConversationById(conversationId);

        if (!isParticipant(conversation, sender)) {
            throw new RuntimeException("You are not a participant of this conversation");
        }

        Message message = new Message();
        message.setConversation(conversation);
        message.setSender(sender);
        message.setContent(request.getContent());
        message.setRead(false);

        Message saved = messageRepository.save(message);

        // Update conversation's last message and update timestamp
        conversation.setLastMessage(request.getContent());
        conversationRepository.save(conversation);

        // Notify the other participant
        AuthUser recipient = conversation.getUserOne().getId().equals(sender.getId())
                ? conversation.getUserTwo()
                : conversation.getUserOne();

        notificationService.createNotification(
                recipient.getUsername(),
                username,
                NotificationType.MESSAGE,
                "sent you a message",
                conversationId
        );

        return toMessageResponse(saved);
    }

    @Override
    @Transactional
    public void markMessagesAsRead(Long conversationId, String username) {
        AuthUser user = findUserByUsername(username);
        Conversation conversation = findConversationById(conversationId);

        if (!isParticipant(conversation, user)) {
            throw new RuntimeException("You are not a participant of this conversation");
        }

        messageRepository.findByConversationOrderByCreatedAtAsc(conversation)
                .stream()
                .filter(m -> !m.getSender().getId().equals(user.getId()) && !m.isRead())
                .forEach(m -> {
                    m.setRead(true);
                    messageRepository.save(m);
                });
    }

    // ── Helpers ────────────────────────────────────────────────────────────────

    private AuthUser findUserByUsername(String username) {
        return authUserRepository.findByUsername(username)
                .or(() -> authUserRepository.findByEmail(username))
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
    }

    private Conversation findConversationById(Long conversationId) {
        return conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation not found with id: " + conversationId));
    }

    private boolean isParticipant(Conversation conversation, AuthUser user) {
        return conversation.getUserOne().getId().equals(user.getId())
                || conversation.getUserTwo().getId().equals(user.getId());
    }

    private UserSummaryResponse toUserSummary(AuthUser user) {
        return UserSummaryResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }

    private ConversationResponse toConversationResponse(Conversation conversation, AuthUser currentUser) {
        AuthUser participant = conversation.getUserOne().getId().equals(currentUser.getId())
                ? conversation.getUserTwo()
                : conversation.getUserOne();

        long unread = messageRepository.countByConversationAndIsReadFalse(conversation);

        return ConversationResponse.builder()
                .conversationId(conversation.getConversationId())
                .participant(toUserSummary(participant))
                .lastMessage(conversation.getLastMessage())
                .unreadCount((int) unread)
                .updatedAt(conversation.getUpdatedAt())
                .build();
    }

    private MessageResponse toMessageResponse(Message message) {
        return MessageResponse.builder()
                .messageId(message.getMessageId())
                .conversationId(message.getConversation().getConversationId())
                .sender(toUserSummary(message.getSender()))
                .content(message.getContent())
                .isRead(message.isRead())
                .createdAt(message.getCreatedAt())
                .build();
    }
}
