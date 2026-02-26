package com.linkbharat.linkbharatbackend.domain.entity;

import com.linkbharat.linkbharatbackend.audit.BaseEntity;
import com.linkbharat.linkbharatbackend.domain.enums.NotificationType;
import jakarta.persistence.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "notifications")
public class Notification extends BaseEntity<String> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notificationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id", nullable = false)
    private AuthUser recipient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private AuthUser sender;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    @Column(nullable = false)
    private String content;

    private Long referenceId;

    @Column(nullable = false)
    private boolean isRead = false;
}
