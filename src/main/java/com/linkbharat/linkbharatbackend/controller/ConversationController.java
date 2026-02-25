package com.linkbharat.linkbharatbackend.controller;

import com.linkbharat.linkbharatbackend.domain.model.ConversationResponse;
import com.linkbharat.linkbharatbackend.domain.model.MessageRequest;
import com.linkbharat.linkbharatbackend.domain.model.MessageResponse;
import com.linkbharat.linkbharatbackend.service.ConversationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/conversations")
@RequiredArgsConstructor
@Tag(name = "Messaging & Conversations", description = "APIs for managing conversations and messages between connected users.")
@SecurityRequirement(name = "bearerAuth")
public class ConversationController {

    private final ConversationService conversationService;

    @Operation(
            summary = "Get all conversations",
            description = "Returns all conversations for the authenticated user, ordered by most recent activity.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Conversations retrieved",
                            content = @Content(schema = @Schema(implementation = ConversationResponse.class)))
            }
    )
    @GetMapping
    public ResponseEntity<List<ConversationResponse>> getConversations(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(conversationService.getConversations(userDetails.getUsername()));
    }

    @Operation(
            summary = "Get or create a conversation with a user",
            description = "Finds an existing conversation with the specified user or creates a new one.",
            parameters = {
                    @Parameter(name = "userId", description = "ID of the user to start or resume a conversation with", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Conversation found or created",
                            content = @Content(schema = @Schema(implementation = ConversationResponse.class)))
            }
    )
    @PostMapping("/with/{userId}")
    public ResponseEntity<ConversationResponse> getOrCreateConversation(
            @PathVariable Long userId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(conversationService.getOrCreateConversation(userId, userDetails.getUsername()));
    }

    @Operation(
            summary = "Get messages in a conversation",
            description = "Returns all messages in the specified conversation, ordered oldest-first.",
            parameters = {
                    @Parameter(name = "conversationId", description = "ID of the conversation", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Messages retrieved",
                            content = @Content(schema = @Schema(implementation = MessageResponse.class))),
                    @ApiResponse(responseCode = "403", description = "Not a participant", content = @Content)
            }
    )
    @GetMapping("/{conversationId}/messages")
    public ResponseEntity<List<MessageResponse>> getMessages(
            @PathVariable Long conversationId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(conversationService.getMessages(conversationId, userDetails.getUsername()));
    }

    @Operation(
            summary = "Send a message in a conversation",
            description = "Sends a new message to the specified conversation and notifies the recipient.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Message sent",
                            content = @Content(schema = @Schema(implementation = MessageResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid message content", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Not a participant", content = @Content)
            }
    )
    @PostMapping("/{conversationId}/messages")
    public ResponseEntity<MessageResponse> sendMessage(
            @PathVariable Long conversationId,
            @Valid @RequestBody MessageRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        MessageResponse response = conversationService.sendMessage(conversationId, request, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Mark all messages in a conversation as read",
            description = "Marks all unread messages from the other participant as read.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Messages marked as read"),
                    @ApiResponse(responseCode = "403", description = "Not a participant", content = @Content)
            }
    )
    @PutMapping("/{conversationId}/read")
    public ResponseEntity<Void> markAsRead(
            @PathVariable Long conversationId,
            @AuthenticationPrincipal UserDetails userDetails) {
        conversationService.markMessagesAsRead(conversationId, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
}
