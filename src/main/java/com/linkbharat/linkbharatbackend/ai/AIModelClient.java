package com.linkbharat.linkbharatbackend.ai;

public interface AIModelClient {
    String generate(String systemPrompt, String userPrompt);
}
