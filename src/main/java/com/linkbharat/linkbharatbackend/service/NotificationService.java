package com.linkbharat.linkbharatbackend.service;

import com.linkbharat.linkbharatbackend.domain.enums.NotificationType;
import com.linkbharat.linkbharatbackend.domain.model.NotificationResponse;

import java.util.List;

public interface NotificationService {
    List<NotificationResponse> getNotifications(String username, int page, int size);
    List<NotificationResponse> getUnreadNotifications(String username);
    NotificationResponse markAsRead(Long notificationId, String username);
    void markAllAsRead(String username);
    long getUnreadCount(String username);
    void createNotification(String recipientUsername, String senderUsername, NotificationType type, String content, Long referenceId);
}
