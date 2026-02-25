package com.linkbharat.linkbharatbackend.controller;

import com.linkbharat.linkbharatbackend.domain.model.CommentRequest;
import com.linkbharat.linkbharatbackend.domain.model.CommentResponse;
import com.linkbharat.linkbharatbackend.domain.model.PostRequest;
import com.linkbharat.linkbharatbackend.domain.model.PostResponse;
import com.linkbharat.linkbharatbackend.service.PostService;
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
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@Tag(name = "Posts & Feed", description = "APIs for creating, reading, updating, liking, commenting, and sharing posts in the feed.")
@SecurityRequirement(name = "bearerAuth")
public class PostController {

    private final PostService postService;

    @Operation(
            summary = "Get paginated feed",
            description = "Retrieves a paginated list of posts for the authenticated user's feed, ordered by newest first.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Feed retrieved successfully",
                            content = @Content(schema = @Schema(implementation = PostResponse.class)))
            }
    )
    @GetMapping
    public ResponseEntity<List<PostResponse>> getFeed(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(postService.getFeed(page, size, userDetails.getUsername()));
    }

    @Operation(
            summary = "Create a new post",
            description = "Creates a new post for the authenticated user with optional image.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Post created successfully",
                            content = @Content(schema = @Schema(implementation = PostResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid post data", content = @Content)
            }
    )
    @PostMapping
    public ResponseEntity<PostResponse> createPost(
            @Valid @RequestBody PostRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        PostResponse response = postService.createPost(request, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Get a post by ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Post retrieved",
                            content = @Content(schema = @Schema(implementation = PostResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Post not found", content = @Content)
            }
    )
    @GetMapping("/{postId}")
    public ResponseEntity<PostResponse> getPostById(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(postService.getPostById(postId, userDetails.getUsername()));
    }

    @Operation(
            summary = "Get posts by a specific user",
            parameters = {
                    @Parameter(name = "userId", description = "ID of the user whose posts to retrieve", required = true)
            }
    )
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PostResponse>> getPostsByUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(postService.getPostsByUser(userId, page, size, userDetails.getUsername()));
    }

    @Operation(
            summary = "Update an existing post",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Post updated",
                            content = @Content(schema = @Schema(implementation = PostResponse.class))),
                    @ApiResponse(responseCode = "403", description = "Not authorized", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Post not found", content = @Content)
            }
    )
    @PutMapping("/{postId}")
    public ResponseEntity<PostResponse> updatePost(
            @PathVariable Long postId,
            @Valid @RequestBody PostRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(postService.updatePost(postId, request, userDetails.getUsername()));
    }

    @Operation(
            summary = "Delete a post",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Post deleted"),
                    @ApiResponse(responseCode = "403", description = "Not authorized", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Post not found", content = @Content)
            }
    )
    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetails userDetails) {
        postService.deletePost(postId, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Toggle like on a post",
            description = "Likes the post if not already liked, or removes the like if already liked.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Like toggled successfully")
            }
    )
    @PostMapping("/{postId}/like")
    public ResponseEntity<Map<String, Object>> toggleLike(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(postService.toggleLike(postId, userDetails.getUsername()));
    }

    @Operation(
            summary = "Get all comments for a post",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Comments retrieved",
                            content = @Content(schema = @Schema(implementation = CommentResponse.class)))
            }
    )
    @GetMapping("/{postId}/comments")
    public ResponseEntity<List<CommentResponse>> getComments(@PathVariable Long postId) {
        return ResponseEntity.ok(postService.getComments(postId));
    }

    @Operation(
            summary = "Add a comment to a post",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Comment added",
                            content = @Content(schema = @Schema(implementation = CommentResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid comment", content = @Content)
            }
    )
    @PostMapping("/{postId}/comments")
    public ResponseEntity<CommentResponse> addComment(
            @PathVariable Long postId,
            @Valid @RequestBody CommentRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        CommentResponse response = postService.addComment(postId, request, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Share a post",
            description = "Increments the share count of a post and notifies the original author.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Post shared",
                            content = @Content(schema = @Schema(implementation = PostResponse.class)))
            }
    )
    @PostMapping("/{postId}/share")
    public ResponseEntity<PostResponse> sharePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(postService.sharePost(postId, userDetails.getUsername()));
    }
}
