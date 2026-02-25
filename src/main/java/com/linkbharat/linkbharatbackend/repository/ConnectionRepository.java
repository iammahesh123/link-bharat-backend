package com.linkbharat.linkbharatbackend.repository;

import com.linkbharat.linkbharatbackend.domain.entity.AuthUser;
import com.linkbharat.linkbharatbackend.domain.entity.Connection;
import com.linkbharat.linkbharatbackend.domain.enums.ConnectionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConnectionRepository extends JpaRepository<Connection, Long> {

    Optional<Connection> findByRequesterAndReceiver(AuthUser requester, AuthUser receiver);

    // Get all accepted connections for a user
    @Query("SELECT c FROM Connection c WHERE (c.requester = :user OR c.receiver = :user) AND c.status = :status")
    Page<Connection> findByUserAndStatus(@Param("user") AuthUser user, @Param("status") ConnectionStatus status, Pageable pageable);

    @Query("SELECT c FROM Connection c WHERE (c.requester = :user OR c.receiver = :user) AND c.status = :status")
    List<Connection> findByUserAndStatus(@Param("user") AuthUser user, @Param("status") ConnectionStatus status);

    // Get incoming pending requests (invitations) for a user
    List<Connection> findByReceiverAndStatus(AuthUser receiver, ConnectionStatus status);

    // Get outgoing pending requests for a user
    List<Connection> findByRequesterAndStatus(AuthUser requester, ConnectionStatus status);

    // Check if two users are connected or have a pending request
    @Query("SELECT c FROM Connection c WHERE (c.requester = :user1 AND c.receiver = :user2) OR (c.requester = :user2 AND c.receiver = :user1)")
    Optional<Connection> findConnectionBetweenUsers(@Param("user1") AuthUser user1, @Param("user2") AuthUser user2);

    // Count accepted connections for a user
    @Query("SELECT COUNT(c) FROM Connection c WHERE (c.requester = :user OR c.receiver = :user) AND c.status = 'ACCEPTED'")
    long countAcceptedConnectionsForUser(@Param("user") AuthUser user);
}
