package com.linkbharat.linkbharatbackend.domain.model.ai;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SmartReplyResponse {
    /** Three suggested quick-reply strings. */
    private List<String> replies;
}
