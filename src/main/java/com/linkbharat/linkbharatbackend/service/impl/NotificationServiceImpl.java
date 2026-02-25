package com.linkbharat.linkbharatbackend.service.impl;

import com.linkbharat.linkbharatbackend.domain.entity.AuthUser;
import com.linkbharat.linkbharatbackend.domain.entity.Notification;
import com.linkbharat.linkbharatbackend.domain.enums.NotificationType;
import com.linkbharat.linkbharatbackend.domain.model.NotificationResponse;
import com.linkbharat.linkbharatbackend.domain.model.UserSummaryResponse;
import com.linkbharat.linkbharatbackend.exceptions.ResourceNotFoundException;
import com.linkbharat.linkbharatbackend.repository.AuthUserRepository;
import com.linkbharat.linkbharatbackend.repository.NotificationRepository;
import com.linkbharat.linkbharatbackend.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final AuthUserRepository authUserRepository;

    @Override
    public List<NotificationResponse> getNotifications(String username, int page, int size) {
        AuthUser user = findUserByUsername(username);
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return notificationRepository.findByRecipientOrderByCreatedAtDesc(user, pageRequest)
                .getContent()
                .stream()
                .map(this::toNotificationResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<NotificationResponse> getUnreadNotifications(String username) {
        AuthUser user = findUserByUsername(username);
        PageRequest pageRequest = PageRequest.of(0, 50, Sort.by(Sort.Direction.DESC, "createdAt"));
        return notificationRepository.findByRecipientAndIsReadFalseOrderByCreatedAtDesc(user, pageRequest)
                .getContent()
                .stream()
                .map(this::toNotificationResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public NotificationResponse markAsRead(Long notificationId, String username) {
        AuthUser user = findUserByUsername(username);
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + notificationId));

        if (!notification.getRecipient().getId().equals(user.getId())) {
            throw new RuntimeException("You are not authorized to update this notification");
        }

        notification.setRead(true);
        return toNotificationResponse(notificationRepository.save(notification));
    }

    @Override
    @Transactional
    public void markAllAsRead(String username) {
        AuthUser user = findUserByUsername(username);
        notificationRepository.markAllAsReadForUser(user);
    }

    @Override
    public long getUnreadCount(String username) {
        AuthUser user = findUserByUsername(username);
        return notificationRepository.countByRecipientAndIsReadFalse(user);
    }

    @Override
    @Transactional
    public void createNotification(String recipientUsername, String senderUsername,
                                   NotificationType type, String content, Long referenceId) {
        AuthUser recipient = findUserByUsername(recipientUsername);

        Notification notification = new Notification();
        notification.setRecipient(recipient);
        notification.setType(type);
        notification.setContent(content);
        notification.setReferenceId(referenceId);
        notification.setRead(false);

        if (senderUsername != null) {
            authUserRepository.findByUsername(senderUsername)
                    .or(() -> authUserRepository.findByEmail(senderUsername))
                    .ifPresent(notification::setSender);
        }

        notificationRepository.save(notification);
    }

    // ── Helpers ────────────────────────────────────────────────────────────────

    private AuthUser findUserByUsername(String username) {
        return authUserRepository.findByUsername(username)
                .or(() -> authUserRepository.findByEmail(username))
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
    }

    private UserSummaryResponse toUserSummary(AuthUser user) {
        if (user == null) return null;
        return UserSummaryResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }

    private NotificationResponse toNotificationResponse(Notification notification) {
        return NotificationResponse.builder()
                .notificationId(notification.getNotificationId())
                .sender(toUserSummary(notification.getSender()))
                .type(notification.getType())
                .content(notification.getContent())
                .referenceId(notification.getReferenceId())
                .isRead(notification.isRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
