package com.linkbharat.linkbharatbackend.service.impl;

import com.linkbharat.linkbharatbackend.domain.entity.AuthUser;
import com.linkbharat.linkbharatbackend.domain.entity.Comment;
import com.linkbharat.linkbharatbackend.domain.entity.Post;
import com.linkbharat.linkbharatbackend.domain.entity.PostLike;
import com.linkbharat.linkbharatbackend.domain.enums.NotificationType;
import com.linkbharat.linkbharatbackend.domain.model.CommentRequest;
import com.linkbharat.linkbharatbackend.domain.model.CommentResponse;
import com.linkbharat.linkbharatbackend.domain.model.PostRequest;
import com.linkbharat.linkbharatbackend.domain.model.PostResponse;
import com.linkbharat.linkbharatbackend.domain.model.UserSummaryResponse;
import com.linkbharat.linkbharatbackend.exceptions.ResourceNotFoundException;
import com.linkbharat.linkbharatbackend.repository.AuthUserRepository;
import com.linkbharat.linkbharatbackend.repository.CommentRepository;
import com.linkbharat.linkbharatbackend.repository.PostLikeRepository;
import com.linkbharat.linkbharatbackend.repository.PostRepository;
import com.linkbharat.linkbharatbackend.service.NotificationService;
import com.linkbharat.linkbharatbackend.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final PostLikeRepository postLikeRepository;
    private final AuthUserRepository authUserRepository;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public PostResponse createPost(PostRequest request, String username) {
        AuthUser author = findUserByUsername(username);

        Post post = new Post();
        post.setAuthor(author);
        post.setContent(request.getContent());
        post.setImageUrl(request.getImageUrl());

        Post saved = postRepository.save(post);
        return toPostResponse(saved, author);
    }

    @Override
    @Transactional
    public PostResponse updatePost(Long postId, PostRequest request, String username) {
        AuthUser user = findUserByUsername(username);
        Post post = findPostById(postId);

        if (!post.getAuthor().getId().equals(user.getId())) {
            throw new RuntimeException("You are not authorized to update this post");
        }

        post.setContent(request.getContent());
        if (request.getImageUrl() != null) {
            post.setImageUrl(request.getImageUrl());
        }

        Post updated = postRepository.save(post);
        return toPostResponse(updated, user);
    }

    @Override
    public PostResponse getPostById(Long postId, String username) {
        AuthUser currentUser = findUserByUsername(username);
        Post post = findPostById(postId);
        return toPostResponse(post, currentUser);
    }

    @Override
    public List<PostResponse> getFeed(int page, int size, String username) {
        AuthUser currentUser = findUserByUsername(username);
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Post> posts = postRepository.findAllByIsDeletedFalseOrderByCreatedAtDesc(pageRequest);
        return posts.getContent().stream()
                .map(p -> toPostResponse(p, currentUser))
                .collect(Collectors.toList());
    }

    @Override
    public List<PostResponse> getPostsByUser(Long userId, int page, int size, String currentUsername) {
        AuthUser currentUser = findUserByUsername(currentUsername);
        AuthUser targetUser = authUserRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Post> posts = postRepository.findByAuthorAndIsDeletedFalseOrderByCreatedAtDesc(targetUser, pageRequest);
        return posts.getContent().stream()
                .map(p -> toPostResponse(p, currentUser))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deletePost(Long postId, String username) {
        AuthUser user = findUserByUsername(username);
        Post post = findPostById(postId);

        if (!post.getAuthor().getId().equals(user.getId())) {
            throw new RuntimeException("You are not authorized to delete this post");
        }

        post.setDeleted(true);
        postRepository.save(post);
    }

    @Override
    @Transactional
    public Map<String, Object> toggleLike(Long postId, String username) {
        AuthUser user = findUserByUsername(username);
        Post post = findPostById(postId);

        Optional<PostLike> existingLike = postLikeRepository.findByPostAndUser(post, user);
        boolean isNowLiked;

        if (existingLike.isPresent()) {
            postLikeRepository.delete(existingLike.get());
            post.setLikesCount(Math.max(0, post.getLikesCount() - 1));
            isNowLiked = false;
        } else {
            PostLike like = new PostLike();
            like.setPost(post);
            like.setUser(user);
            postLikeRepository.save(like);
            post.setLikesCount(post.getLikesCount() + 1);
            isNowLiked = true;

            // Send notification to post author (not to yourself)
            if (!post.getAuthor().getId().equals(user.getId())) {
                notificationService.createNotification(
                        post.getAuthor().getUsername(),
                        username,
                        NotificationType.LIKE,
                        "liked your post",
                        postId
                );
            }
        }

        postRepository.save(post);

        Map<String, Object> result = new HashMap<>();
        result.put("isLiked", isNowLiked);
        result.put("likesCount", post.getLikesCount());
        return result;
    }

    @Override
    @Transactional
    public CommentResponse addComment(Long postId, CommentRequest request, String username) {
        AuthUser user = findUserByUsername(username);
        Post post = findPostById(postId);

        Comment comment = new Comment();
        comment.setPost(post);
        comment.setAuthor(user);
        comment.setContent(request.getContent());

        Comment saved = commentRepository.save(comment);

        // Notify post author
        if (!post.getAuthor().getId().equals(user.getId())) {
            notificationService.createNotification(
                    post.getAuthor().getUsername(),
                    username,
                    NotificationType.COMMENT,
                    "commented on your post",
                    postId
            );
        }

        return toCommentResponse(saved);
    }

    @Override
    public List<CommentResponse> getComments(Long postId) {
        Post post = findPostById(postId);
        return commentRepository.findByPostOrderByCreatedAtAsc(post).stream()
                .filter(c -> !c.isDeleted())
                .map(this::toCommentResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PostResponse sharePost(Long postId, String username) {
        AuthUser user = findUserByUsername(username);
        Post originalPost = findPostById(postId);

        originalPost.setSharesCount(originalPost.getSharesCount() + 1);
        postRepository.save(originalPost);

        // Notify the original post author
        if (!originalPost.getAuthor().getId().equals(user.getId())) {
            notificationService.createNotification(
                    originalPost.getAuthor().getUsername(),
                    username,
                    NotificationType.SHARE,
                    "shared your post",
                    postId
            );
        }

        return toPostResponse(originalPost, user);
    }

    // ── Helpers ────────────────────────────────────────────────────────────────

    private AuthUser findUserByUsername(String username) {
        return authUserRepository.findByUsername(username)
                .or(() -> authUserRepository.findByEmail(username))
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
    }

    private Post findPostById(Long postId) {
        return postRepository.findById(postId)
                .filter(p -> !p.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));
    }

    private UserSummaryResponse toUserSummary(AuthUser user) {
        return UserSummaryResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }

    private CommentResponse toCommentResponse(Comment comment) {
        return CommentResponse.builder()
                .commentId(comment.getCommentId())
                .author(toUserSummary(comment.getAuthor()))
                .content(comment.getContent())
                .likesCount(comment.getLikesCount())
                .createdAt(comment.getCreatedAt())
                .build();
    }

    private PostResponse toPostResponse(Post post, AuthUser currentUser) {
        boolean isLiked = postLikeRepository.existsByPostAndUser(post, currentUser);
        long commentsCount = commentRepository.countByPostAndIsDeletedFalse(post);

        List<CommentResponse> recentComments = commentRepository
                .findByPostOrderByCreatedAtAsc(post)
                .stream()
                .filter(c -> !c.isDeleted())
                .limit(2)
                .map(this::toCommentResponse)
                .collect(Collectors.toList());

        return PostResponse.builder()
                .postId(post.getPostId())
                .author(toUserSummary(post.getAuthor()))
                .content(post.getContent())
                .imageUrl(post.getImageUrl())
                .likesCount(post.getLikesCount())
                .sharesCount(post.getSharesCount())
                .commentsCount((int) commentsCount)
                .isLiked(isLiked)
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .recentComments(recentComments)
                .build();
    }
}
