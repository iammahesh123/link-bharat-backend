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
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user-profiles")
@Tag(name = "User Profile Management", description = "Operations related to user profile creation, retrieval, update, deletion, and pagination.")
public class UserProfileController {

    private final UserProfileService userProfileService;

    @Operation(
            summary = "Create a new user profile",
            description = "Creates and stores a new user profile in the system.",
            requestBody = @RequestBody(
                    required = true,
                    description = "Details for creating a user profile",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                            {
                              "firstName": "Mahesh",
                              "lastName": "Kadambala",
                              "email": "mahesh@example.com",
                              "phoneNumber": "+91-9876543210",
                              "designation": "Software Developer",
                              "company": "Link Bharat Pvt Ltd"
                            }
                            """)
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "User profile created successfully",
                            content = @Content(schema = @Schema(implementation = UserProfileResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content)
            }
    )
    @PostMapping
    public ResponseEntity<UserProfileResponse> createUserProfile(@RequestBody UserProfileRequest userProfileRequest) {
        return ResponseEntity.ok(userProfileService.createUserProfile(userProfileRequest));
    }

    @Operation(
            summary = "Update an existing user profile",
            description = "Updates the user profile identified by its ID.",
            parameters = {
                    @Parameter(name = "user_profile_id", description = "Unique ID of the user profile", example = "101", required = true)
            },
            requestBody = @RequestBody(
                    required = true,
                    description = "Updated profile information",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                            {
                              "firstName": "Mahesh",
                              "lastName": "Kumar",
                              "email": "mahesh.kumar@example.com",
                              "phoneNumber": "+91-9876543211",
                              "designation": "Senior Developer",
                              "company": "Link Bharat Technologies"
                            }
                            """)
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Profile updated successfully",
                            content = @Content(schema = @Schema(implementation = UserProfileResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Profile not found", content = @Content)
            }
    )
    @PutMapping("/{user_profile_id}")
    public ResponseEntity<UserProfileResponse> updateUserProfile(
            @PathVariable("user_profile_id") Long userProfileId,
            @RequestBody UserProfileRequest userProfileRequest) {
        return ResponseEntity.ok(userProfileService.updateUserProfile(userProfileId, userProfileRequest));
    }


    @Operation(
            summary = "Get user profile by ID",
            description = "Retrieves a user profile using its unique identifier.",
            parameters = {
                    @Parameter(name = "user_profile_id", description = "Unique ID of the user profile", example = "101", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Profile retrieved successfully",
                            content = @Content(schema = @Schema(implementation = UserProfileResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Profile not found", content = @Content)
            }
    )
    @GetMapping("/{user_profile_id}")
    public ResponseEntity<UserProfileResponse> getUserProfile(@PathVariable("user_profile_id") Long userProfileId) {
        return ResponseEntity.ok(userProfileService.getUserProfileById(userProfileId));
    }

    @Operation(
            summary = "Get paginated list of user profiles",
            description = "Retrieves a paginated and optionally sorted list of user profiles.",
            parameters = {
                    @Parameter(name = "pageNumber", description = "Page number (0-indexed)", example = "0"),
                    @Parameter(name = "pageSize", description = "Number of items per page", example = "10"),
                    @Parameter(name = "sortColumn", description = "Column name to sort by", example = "firstName"),
                    @Parameter(name = "orderBY", description = "Sorting order (ASC or DESC)", example = "ASC")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Profiles retrieved successfully",
                            content = @Content(schema = @Schema(implementation = PaginationResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid pagination parameters", content = @Content)
            }
    )
    @GetMapping
    public ResponseEntity<PaginationResponse<UserProfileResponse>> getUserProfiles(
            @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
            @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @RequestParam(value = "sortColumn", required = false) String sortColumn,
            @RequestParam(value = "orderBY", required = false) OrderBy orderBy) {

        PageModel pageModel = new PageModel(pageNumber, pageSize, sortColumn, orderBy);
        return ResponseEntity.ok(userProfileService.getAllUserProfiles(pageModel));
    }

    @Operation(
            summary = "Delete user profile by ID",
            description = "Deletes the user profile identified by the given ID.",
            parameters = {
                    @Parameter(name = "user_profile_id", description = "Unique ID of the user profile to delete", example = "101", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "202", description = "Profile deletion accepted"),
                    @ApiResponse(responseCode = "404", description = "Profile not found", content = @Content)
            }
    )
    @DeleteMapping("/{user_profile_id}")
    public ResponseEntity<HttpStatus> deleteUserProfile(@PathVariable("user_profile_id") Long userProfileId) {
        userProfileService.deleteUserProfile(userProfileId);
        return ResponseEntity.ok(HttpStatus.ACCEPTED);
    }
}
