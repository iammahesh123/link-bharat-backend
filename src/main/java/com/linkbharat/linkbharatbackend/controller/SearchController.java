package com.linkbharat.linkbharatbackend.controller;

import com.linkbharat.linkbharatbackend.domain.model.UserSummaryResponse;
import com.linkbharat.linkbharatbackend.service.SearchService;
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

@CrossOrigin
@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
@Tag(name = "Search", description = "APIs for searching users across the platform.")
@SecurityRequirement(name = "bearerAuth")
public class SearchController {

    private final SearchService searchService;

    @Operation(
            summary = "Search users",
            description = "Searches users by username or email matching the provided query string. Excludes the current user from results.",
            parameters = {
                    @Parameter(name = "q", description = "Search query string (matches username or email)", example = "mahesh", required = true),
                    @Parameter(name = "page", description = "Page number (0-indexed)", example = "0"),
                    @Parameter(name = "size", description = "Number of results per page", example = "10")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Search results returned",
                            content = @Content(schema = @Schema(implementation = UserSummaryResponse.class)))
            }
    )
    @GetMapping("/users")
    public ResponseEntity<List<UserSummaryResponse>> searchUsers(
            @RequestParam(value = "q", defaultValue = "") String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(searchService.searchUsers(query, page, size, userDetails.getUsername()));
    }
}
