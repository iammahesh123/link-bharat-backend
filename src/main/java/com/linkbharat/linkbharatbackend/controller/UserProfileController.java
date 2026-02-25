package com.linkbharat.linkbharatbackend.controller;

import com.linkbharat.linkbharatbackend.domain.enums.OrderBy;
import com.linkbharat.linkbharatbackend.domain.model.PageModel;
import com.linkbharat.linkbharatbackend.domain.model.PaginationResponse;
import com.linkbharat.linkbharatbackend.domain.model.UserProfileRequest;
import com.linkbharat.linkbharatbackend.domain.model.UserProfileResponse;
import com.linkbharat.linkbharatbackend.service.UserProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user-profiles")
@Tag(name = "User Profile Management", description = "Operations related to user profile creation, retrieval, update, deletion, and pagination.")
@SecurityRequirement(name = "bearerAuth")
public class UserProfileController {

    private final UserProfileService userProfileService;

    // ─── Current-user endpoints ──────────────────────────────────────────────

    @Operation(
            summary = "Get current user's profile",
            description = "Returns the full profile for the currently authenticated user.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Profile retrieved",
                            content = @Content(schema = @Schema(implementation = UserProfileResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Profile not found", content = @Content)
            }
    )
    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getMyProfile(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(userProfileService.getMyProfile(userDetails.getUsername()));
    }

    @Operation(
            summary = "Create or update current user's profile",
            description = "Creates the profile if it doesn't exist, otherwise updates it. Only supplied fields are changed.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Profile saved",
                            content = @Content(schema = @Schema(implementation = UserProfileResponse.class)))
            }
    )
    @PostMapping("/me")
    public ResponseEntity<UserProfileResponse> upsertMyProfile(
            @org.springframework.web.bind.annotation.RequestBody UserProfileRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(userProfileService.upsertMyProfile(userDetails.getUsername(), request));
    }

    // ─── Lookup by AuthUser ID ────────────────────────────────────────────────

    @Operation(
            summary = "Get profile by user ID",
            description = "Returns the full profile for the given AuthUser ID (the ID used in frontend URLs like /profile/{userId}).",
            parameters = {
                    @Parameter(name = "userId", description = "AuthUser ID of the target user", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Profile retrieved",
                            content = @Content(schema = @Schema(implementation = UserProfileResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Profile not found", content = @Content)
            }
    )
    @GetMapping("/user/{userId}")
    public ResponseEntity<UserProfileResponse> getProfileByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(userProfileService.getProfileByAuthUserId(userId));
    }

    // ─── Admin / generic CRUD ─────────────────────────────────────────────────

    @PostMapping
    public ResponseEntity<UserProfileResponse> createUserProfile(
            @org.springframework.web.bind.annotation.RequestBody UserProfileRequest userProfileRequest) {
        return ResponseEntity.ok(userProfileService.createUserProfile(userProfileRequest));
    }

    @PutMapping("/{user_profile_id}")
    public ResponseEntity<UserProfileResponse> updateUserProfile(
            @PathVariable("user_profile_id") Long userProfileId,
            @org.springframework.web.bind.annotation.RequestBody UserProfileRequest userProfileRequest) {
        return ResponseEntity.ok(userProfileService.updateUserProfile(userProfileId, userProfileRequest));
    }

    @GetMapping("/{user_profile_id}")
    public ResponseEntity<UserProfileResponse> getUserProfile(
            @PathVariable("user_profile_id") Long userProfileId) {
        return ResponseEntity.ok(userProfileService.getUserProfileById(userProfileId));
    }

    @GetMapping
    public ResponseEntity<PaginationResponse<UserProfileResponse>> getUserProfiles(
            @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
            @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @RequestParam(value = "sortColumn", required = false) String sortColumn,
            @RequestParam(value = "orderBY", required = false) OrderBy orderBy) {
        PageModel pageModel = new PageModel(pageNumber, pageSize, sortColumn, orderBy);
        return ResponseEntity.ok(userProfileService.getAllUserProfiles(pageModel));
    }

    @DeleteMapping("/{user_profile_id}")
    public ResponseEntity<HttpStatus> deleteUserProfile(
            @PathVariable("user_profile_id") Long userProfileId) {
        userProfileService.deleteUserProfile(userProfileId);
        return ResponseEntity.ok(HttpStatus.ACCEPTED);
    }
}
