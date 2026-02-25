package com.linkbharat.linkbharatbackend.domain.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MessageRequest {

    @NotBlank(message = "Message content cannot be empty")
    private String content;
}
