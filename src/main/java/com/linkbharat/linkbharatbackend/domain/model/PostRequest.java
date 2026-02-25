package com.linkbharat.linkbharatbackend.domain.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PostRequest {

    @NotBlank(message = "Post content cannot be empty")
    private String content;

    private String imageUrl;
}
