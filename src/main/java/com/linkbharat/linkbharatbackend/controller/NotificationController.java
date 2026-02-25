package com.linkbharat.linkbharatbackend.controller;

import com.linkbharat.linkbharatbackend.domain.model.NotificationResponse;
import com.linkbharat.linkbharatbackend.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications", description = "APIs for fetching, reading, and managing user notifications.")
@SecurityRequirement(name = "bearerAuth")
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(
            summary = "Get all notifications",
            description = "Returns paginated notifications for the authenticated user, ordered newest first.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Notifications retrieved",
                            content = @Content(schema = @Schema(implementation = NotificationResponse.class)))
            }
    )
    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(notificationService.getNotifications(userDetails.getUsername(), page, size));
    }

    @Operation(
            summary = "Get unread notification count",
            description = "Returns the total number of unread notifications for the current user.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Count retrieved")
            }
    )
    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Long>> getUnreadCount(
            @AuthenticationPrincipal UserDetails userDetails) {
        long count = notificationService.getUnreadCount(userDetails.getUsername());
        return ResponseEntity.ok(Map.of("unreadCount", count));
    }

    @Operation(
            summary = "Mark a specific notification as read",
            parameters = {
                    @Parameter(name = "notificationId", description = "ID of the notification to mark as read", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Notification marked as read",
                            content = @Content(schema = @Schema(implementation = NotificationResponse.class))),
                    @ApiResponse(responseCode = "403", description = "Not authorized", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Notification not found", content = @Content)
            }
    )
    @PutMapping("/{notificationId}/read")
    public ResponseEntity<NotificationResponse> markAsRead(
            @PathVariable Long notificationId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(notificationService.markAsRead(notificationId, userDetails.getUsername()));
    }

    @Operation(
            summary = "Mark all notifications as read",
            description = "Marks all unread notifications for the current user as read.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "All notifications marked as read")
            }
    )
    @PutMapping("/mark-all-read")
    public ResponseEntity<Void> markAllAsRead(@AuthenticationPrincipal UserDetails userDetails) {
        notificationService.markAllAsRead(userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
}
