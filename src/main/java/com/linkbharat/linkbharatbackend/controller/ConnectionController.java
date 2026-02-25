package com.linkbharat.linkbharatbackend.controller;

import com.linkbharat.linkbharatbackend.domain.model.ConnectionResponse;
import com.linkbharat.linkbharatbackend.domain.model.UserSummaryResponse;
import com.linkbharat.linkbharatbackend.service.ConnectionService;
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
@RequestMapping("/api/connections")
@RequiredArgsConstructor
@Tag(name = "Connections & Network", description = "APIs for managing professional connections — suggestions, invitations, connect, accept, and remove.")
@SecurityRequirement(name = "bearerAuth")
public class ConnectionController {

    private final ConnectionService connectionService;

    @Operation(
            summary = "Get connection suggestions",
            description = "Returns a paginated list of users the current user is not yet connected to."
    )
    @GetMapping("/suggestions")
    public ResponseEntity<List<UserSummaryResponse>> getSuggestions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(connectionService.getSuggestions(userDetails.getUsername(), page, size));
    }

    @Operation(
            summary = "Get all accepted connections",
            description = "Returns the list of all accepted connections for the current user."
    )
    @GetMapping
    public ResponseEntity<List<ConnectionResponse>> getConnections(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(connectionService.getConnections(userDetails.getUsername()));
    }

    @Operation(
            summary = "Get pending incoming invitations",
            description = "Returns all connection requests where the current user is the receiver and the status is pending."
    )
    @GetMapping("/invitations")
    public ResponseEntity<List<ConnectionResponse>> getPendingInvitations(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(connectionService.getPendingInvitations(userDetails.getUsername()));
    }

    @Operation(
            summary = "Send a connection request",
            description = "Sends a connection request to the specified user.",
            parameters = {
                    @Parameter(name = "userId", description = "ID of the user to send a connection request to", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Connection request sent",
                            content = @Content(schema = @Schema(implementation = ConnectionResponse.class))),
                    @ApiResponse(responseCode = "409", description = "Request already exists", content = @Content)
            }
    )
    @PostMapping("/request/{userId}")
    public ResponseEntity<ConnectionResponse> sendConnectionRequest(
            @PathVariable Long userId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(connectionService.sendConnectionRequest(userId, userDetails.getUsername()));
    }

    @Operation(
            summary = "Accept a connection request",
            description = "Accepts an incoming connection request by its connection ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Connection accepted",
                            content = @Content(schema = @Schema(implementation = ConnectionResponse.class))),
                    @ApiResponse(responseCode = "403", description = "Not authorized to accept this request", content = @Content)
            }
    )
    @PutMapping("/accept/{connectionId}")
    public ResponseEntity<ConnectionResponse> acceptConnection(
            @PathVariable Long connectionId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(connectionService.acceptConnectionRequest(connectionId, userDetails.getUsername()));
    }

    @Operation(
            summary = "Ignore / decline a connection request",
            description = "Declines an incoming connection request by its connection ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Request ignored"),
                    @ApiResponse(responseCode = "403", description = "Not authorized", content = @Content)
            }
    )
    @PutMapping("/ignore/{connectionId}")
    public ResponseEntity<ConnectionResponse> ignoreConnection(
            @PathVariable Long connectionId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(connectionService.ignoreConnectionRequest(connectionId, userDetails.getUsername()));
    }

    @Operation(
            summary = "Remove an existing connection",
            description = "Removes an accepted connection between the current user and the specified user.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Connection removed"),
                    @ApiResponse(responseCode = "404", description = "Connection not found", content = @Content)
            }
    )
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> removeConnection(
            @PathVariable Long userId,
            @AuthenticationPrincipal UserDetails userDetails) {
        connectionService.removeConnection(userId, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Get connection status with a specific user",
            description = "Returns the connection status (NONE, OUTGOING_PENDING, INCOMING_PENDING, CONNECTED) between the current user and the target user."
    )
    @GetMapping("/status/{userId}")
    public ResponseEntity<Map<String, String>> getConnectionStatus(
            @PathVariable Long userId,
            @AuthenticationPrincipal UserDetails userDetails) {
        String status = connectionService.getConnectionStatus(userId, userDetails.getUsername());
        return ResponseEntity.ok(Map.of("status", status));
    }
}
