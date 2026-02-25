package com.linkbharat.linkbharatbackend.service.impl;

import com.linkbharat.linkbharatbackend.domain.entity.AuthUser;
import com.linkbharat.linkbharatbackend.domain.entity.Connection;
import com.linkbharat.linkbharatbackend.domain.enums.ConnectionStatus;
import com.linkbharat.linkbharatbackend.domain.enums.NotificationType;
import com.linkbharat.linkbharatbackend.domain.model.ConnectionResponse;
import com.linkbharat.linkbharatbackend.domain.model.UserSummaryResponse;
import com.linkbharat.linkbharatbackend.exceptions.ResourceNotFoundException;
import com.linkbharat.linkbharatbackend.repository.AuthUserRepository;
import com.linkbharat.linkbharatbackend.repository.ConnectionRepository;
import com.linkbharat.linkbharatbackend.service.ConnectionService;
import com.linkbharat.linkbharatbackend.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConnectionServiceImpl implements ConnectionService {

    private final ConnectionRepository connectionRepository;
    private final AuthUserRepository authUserRepository;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public ConnectionResponse sendConnectionRequest(Long receiverId, String username) {
        AuthUser requester = findUserByUsername(username);
        AuthUser receiver = authUserRepository.findById(receiverId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + receiverId));

        if (requester.getId().equals(receiver.getId())) {
            throw new RuntimeException("You cannot send a connection request to yourself");
        }

        Optional<Connection> existing = connectionRepository.findConnectionBetweenUsers(requester, receiver);
        if (existing.isPresent()) {
            throw new RuntimeException("A connection request already exists between these users");
        }

        Connection connection = new Connection();
        connection.setRequester(requester);
        connection.setReceiver(receiver);
        connection.setStatus(ConnectionStatus.PENDING);

        Connection saved = connectionRepository.save(connection);

        notificationService.createNotification(
                receiver.getUsername(),
                username,
                NotificationType.CONNECTION_REQUEST,
                "sent you a connection request",
                saved.getConnectionId()
        );

        return toConnectionResponse(saved, requester);
    }

    @Override
    @Transactional
    public ConnectionResponse acceptConnectionRequest(Long connectionId, String username) {
        AuthUser user = findUserByUsername(username);
        Connection connection = findConnectionById(connectionId);

        if (!connection.getReceiver().getId().equals(user.getId())) {
            throw new RuntimeException("You are not authorized to accept this request");
        }

        connection.setStatus(ConnectionStatus.ACCEPTED);
        Connection updated = connectionRepository.save(connection);

        notificationService.createNotification(
                connection.getRequester().getUsername(),
                username,
                NotificationType.CONNECTION_ACCEPTED,
                "accepted your connection request",
                connectionId
        );

        return toConnectionResponse(updated, user);
    }

    @Override
    @Transactional
    public ConnectionResponse ignoreConnectionRequest(Long connectionId, String username) {
        AuthUser user = findUserByUsername(username);
        Connection connection = findConnectionById(connectionId);

        if (!connection.getReceiver().getId().equals(user.getId())) {
            throw new RuntimeException("You are not authorized to ignore this request");
        }

        connection.setStatus(ConnectionStatus.REJECTED);
        return toConnectionResponse(connectionRepository.save(connection), user);
    }

    @Override
    @Transactional
    public void removeConnection(Long userId, String username) {
        AuthUser currentUser = findUserByUsername(username);
        AuthUser targetUser = authUserRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Connection connection = connectionRepository.findConnectionBetweenUsers(currentUser, targetUser)
                .orElseThrow(() -> new ResourceNotFoundException("No connection found between these users"));

        connectionRepository.delete(connection);
    }

    @Override
    public List<ConnectionResponse> getConnections(String username) {
        AuthUser user = findUserByUsername(username);
        return connectionRepository.findByUserAndStatus(user, ConnectionStatus.ACCEPTED)
                .stream()
                .map(c -> toConnectionResponse(c, user))
                .collect(Collectors.toList());
    }

    @Override
    public List<ConnectionResponse> getPendingInvitations(String username) {
        AuthUser user = findUserByUsername(username);
        return connectionRepository.findByReceiverAndStatus(user, ConnectionStatus.PENDING)
                .stream()
                .map(c -> toConnectionResponse(c, user))
                .collect(Collectors.toList());
    }

    @Override
    public List<UserSummaryResponse> getSuggestions(String username, int page, int size) {
        AuthUser currentUser = findUserByUsername(username);

        // Get IDs of users already connected or with pending requests
        List<Long> excludeIds = connectionRepository.findByUserAndStatus(currentUser, ConnectionStatus.ACCEPTED)
                .stream()
                .map(c -> c.getRequester().getId().equals(currentUser.getId())
                        ? c.getReceiver().getId()
                        : c.getRequester().getId())
                .collect(Collectors.toList());

        excludeIds.addAll(
                connectionRepository.findByRequesterAndStatus(currentUser, ConnectionStatus.PENDING)
                        .stream()
                        .map(c -> c.getReceiver().getId())
                        .collect(Collectors.toList())
        );
        excludeIds.addAll(
                connectionRepository.findByReceiverAndStatus(currentUser, ConnectionStatus.PENDING)
                        .stream()
                        .map(c -> c.getRequester().getId())
                        .collect(Collectors.toList())
        );
        excludeIds.add(currentUser.getId());

        PageRequest pageRequest = PageRequest.of(page, size);
        return authUserRepository.findAll(pageRequest)
                .getContent()
                .stream()
                .filter(u -> !excludeIds.contains(u.getId()))
                .map(this::toUserSummary)
                .collect(Collectors.toList());
    }

    @Override
    public String getConnectionStatus(Long targetUserId, String username) {
        AuthUser currentUser = findUserByUsername(username);
        AuthUser targetUser = authUserRepository.findById(targetUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + targetUserId));

        Optional<Connection> connection = connectionRepository.findConnectionBetweenUsers(currentUser, targetUser);
        if (connection.isEmpty()) return "NONE";

        Connection c = connection.get();
        if (c.getStatus() == ConnectionStatus.ACCEPTED) return "CONNECTED";
        if (c.getStatus() == ConnectionStatus.PENDING) {
            return c.getRequester().getId().equals(currentUser.getId())
                    ? "OUTGOING_PENDING"
                    : "INCOMING_PENDING";
        }
        return "NONE";
    }

    // ── Helpers ────────────────────────────────────────────────────────────────

    private AuthUser findUserByUsername(String username) {
        return authUserRepository.findByUsername(username)
                .or(() -> authUserRepository.findByEmail(username))
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
    }

    private Connection findConnectionById(Long connectionId) {
        return connectionRepository.findById(connectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Connection not found with id: " + connectionId));
    }

    private UserSummaryResponse toUserSummary(AuthUser user) {
        return UserSummaryResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }

    private ConnectionResponse toConnectionResponse(Connection connection, AuthUser currentUser) {
        AuthUser otherUser = connection.getRequester().getId().equals(currentUser.getId())
                ? connection.getReceiver()
                : connection.getRequester();

        return ConnectionResponse.builder()
                .connectionId(connection.getConnectionId())
                .requester(toUserSummary(connection.getRequester()))
                .receiver(toUserSummary(connection.getReceiver()))
                .status(connection.getStatus())
                .createdAt(connection.getCreatedAt())
                .otherUser(toUserSummary(otherUser))
                .build();
    }
}
