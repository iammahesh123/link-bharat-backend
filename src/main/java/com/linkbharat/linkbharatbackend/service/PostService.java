package com.linkbharat.linkbharatbackend.service;

import com.linkbharat.linkbharatbackend.domain.model.CommentRequest;
import com.linkbharat.linkbharatbackend.domain.model.CommentResponse;
import com.linkbharat.linkbharatbackend.domain.model.PostRequest;
import com.linkbharat.linkbharatbackend.domain.model.PostResponse;

import java.util.List;
import java.util.Map;

public interface PostService {
    PostResponse createPost(PostRequest request, String username);
    PostResponse updatePost(Long postId, PostRequest request, String username);
    PostResponse getPostById(Long postId, String username);
    List<PostResponse> getFeed(int page, int size, String username);
    List<PostResponse> getPostsByUser(Long userId, int page, int size, String currentUsername);
    void deletePost(Long postId, String username);
    Map<String, Object> toggleLike(Long postId, String username);
    CommentResponse addComment(Long postId, CommentRequest request, String username);
    List<CommentResponse> getComments(Long postId);
    PostResponse sharePost(Long postId, String username);
}
