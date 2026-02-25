package com.linkbharat.linkbharatbackend.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponse {
    private Long commentId;
    private UserSummaryResponse author;
    private String content;
    private int likesCount;
    private LocalDateTime createdAt;
}
