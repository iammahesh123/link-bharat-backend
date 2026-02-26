package com.linkbharat.linkbharatbackend.domain.model.ai;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Generic single-string response used by rewrite, autocomplete, summary, connection message. */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TextResponse {
    private String text;
}
