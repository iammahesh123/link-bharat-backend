package com.linkbharat.linkbharatbackend.repository;

import com.linkbharat.linkbharatbackend.domain.entity.AuthUser;
import com.linkbharat.linkbharatbackend.domain.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    // Get all conversations for a user (ordered by last activity)
    @Query("SELECT c FROM Conversation c WHERE (c.userOne = :user OR c.userTwo = :user) ORDER BY c.updatedAt DESC")
    List<Conversation> findByUserOrderByUpdatedAtDesc(@Param("user") AuthUser user);

    // Find an existing conversation between two specific users
    @Query("SELECT c FROM Conversation c WHERE (c.userOne = :user1 AND c.userTwo = :user2) OR (c.userOne = :user2 AND c.userTwo = :user1)")
    Optional<Conversation> findBetweenUsers(@Param("user1") AuthUser user1, @Param("user2") AuthUser user2);
}
