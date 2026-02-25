package com.linkbharat.linkbharatbackend.domain.entity;

import com.linkbharat.linkbharatbackend.audit.BaseEntity;
import com.linkbharat.linkbharatbackend.domain.enums.ConnectionStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "connections", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"requester_id", "receiver_id"})
})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Connection extends BaseEntity<String> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long connectionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id", nullable = false)
    private AuthUser requester;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private AuthUser receiver;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ConnectionStatus status = ConnectionStatus.PENDING;
}
