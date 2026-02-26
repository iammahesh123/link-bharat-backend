package com.linkbharat.linkbharatbackend.service;

import com.linkbharat.linkbharatbackend.domain.model.ai.*;

public interface AIService {

    SmartReplyResponse getSmartReplies(SmartReplyRequest request);

    TextResponse rewriteMessage(RewriteRequest request);

    TextResponse autoComplete(AutoCompleteRequest request);

    TextResponse summarizeConversation(SummaryRequest request);

    TextResponse getConnectionRecommendation(ConnectionRecommendationRequest request);

    TextResponse generateConnectionMessage(ConnectionMessageRequest request);

    ProfileStrengthResponse getProfileStrength(String username);
}
