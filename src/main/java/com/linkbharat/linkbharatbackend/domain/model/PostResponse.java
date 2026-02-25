package com.linkbharat.linkbharatbackend.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostResponse {
    private Long postId;
    private UserSummaryResponse author;
    private String content;
    private String imageUrl;
    private int likesCount;
    private int sharesCount;
    private int commentsCount;
    private boolean isLiked;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<CommentResponse> recentComments;
}
