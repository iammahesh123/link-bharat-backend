package com.linkbharat.linkbharatbackend.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.linkbharat.linkbharatbackend.ai.AIModelClient;
import com.linkbharat.linkbharatbackend.domain.entity.UserProfile;
import com.linkbharat.linkbharatbackend.domain.model.ai.*;
import com.linkbharat.linkbharatbackend.exceptions.ResourceNotFoundException;
import com.linkbharat.linkbharatbackend.repository.UserProfileRepository;
import com.linkbharat.linkbharatbackend.service.AIService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AIServiceImpl implements AIService {

    private final UserProfileRepository userProfileRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final AIModelClient aiModelClient;

    private String callModel(String system, String user) {
        return aiModelClient.generate(system, user);
    }

    // ── Messaging AI ─────────────────────────────────────────────────────────

    @Override
    public SmartReplyResponse getSmartReplies(SmartReplyRequest request) {
        String history = request.getMessages().stream()
                .map(m -> (m.isMe() ? "Me" : m.getSenderName()) + ": " + m.getContent())
                .collect(Collectors.joining("\n"));

        String system = "You are a professional messaging assistant. Given a conversation, suggest 3 short, natural quick-reply options. " +
                "Return ONLY a JSON array of 3 strings, nothing else. Example: [\"Thanks!\", \"Sounds good.\", \"I'll check and get back to you.\"]";

        String userPrompt = "Conversation:\n" + history + "\n\nSuggest 3 quick replies.";
        String raw = callModel(system, userPrompt);

        // Parse JSON array
        try {
            // Strip any markdown code fences if present
            raw = raw.replaceAll("```json\\s*", "").replaceAll("```\\s*", "").trim();
            JsonNode arr = objectMapper.readTree(raw);
            List<String> replies = new ArrayList<>();
            arr.forEach(n -> replies.add(n.asText()));
            return new SmartReplyResponse(replies);
        } catch (Exception e) {
            // Fallback: split by newline
            List<String> fallback = Arrays.stream(raw.split("\n"))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .limit(3)
                    .collect(Collectors.toList());
            return new SmartReplyResponse(fallback);
        }
    }

    @Override
    public TextResponse rewriteMessage(RewriteRequest request) {
        String system = "You are a professional writing assistant. Rewrite the given message in the requested tone. " +
                "Return ONLY the rewritten message text, no explanations.";
        String userPrompt = "Tone: " + request.getTone() + "\nOriginal message: " + request.getDraft() + "\n\nRewrite it.";
        return new TextResponse(callModel(system, userPrompt));
    }

    @Override
    public TextResponse autoComplete(AutoCompleteRequest request) {
        String context = request.getContext() == null ? "" : request.getContext().stream()
                .map(m -> (m.isMe() ? "Me" : m.getSenderName()) + ": " + m.getContent())
                .collect(Collectors.joining("\n"));

        String system = "You are a smart message auto-complete assistant. Given a partial message and conversation context, " +
                "complete the sentence naturally in 1-2 sentences. Return ONLY the completed message, nothing else.";
        String userPrompt = "Context:\n" + context + "\n\nPartial message: " + request.getPartial();
        return new TextResponse(callModel(system, userPrompt));
    }

    @Override
    public TextResponse summarizeConversation(SummaryRequest request) {
        String history = request.getMessages().stream()
                .map(m -> (m.isMe() ? "Me" : m.getSenderName()) + ": " + m.getContent())
                .collect(Collectors.joining("\n"));

        String system = "You are a professional assistant. Summarize the following conversation in 3-5 bullet points highlighting the key topics and decisions.";
        return new TextResponse(callModel(system, "Conversation:\n" + history));
    }

    // ── Networking AI ─────────────────────────────────────────────────────────

    @Override
    public TextResponse getConnectionRecommendation(ConnectionRecommendationRequest req) {
        String system = "You are a professional networking assistant. Given two professionals' profiles, explain in 1-2 sentences why they would benefit from connecting. Be specific and encouraging.";
        String userPrompt = "Person A: " + req.getCurrentUserName() + " — " + req.getCurrentUserHeadline() +
                ". Skills: " + req.getCurrentUserSkills() +
                "\nPerson B: " + req.getSuggestedUserName() + " — " + req.getSuggestedUserHeadline() +
                "\n\nWhy should Person A connect with Person B?";
        return new TextResponse(callModel(system, userPrompt));
    }

    @Override
    public TextResponse generateConnectionMessage(ConnectionMessageRequest req) {
        String system = "You are a professional networking assistant. Write a warm, personalised, and concise connection request note (max 60 words). Do not include a subject line.";
        String userPrompt = "I am " + req.getMyName() + " (" + req.getMyHeadline() + ") and I want to connect with " +
                req.getRecipientName() + " (" + req.getRecipientHeadline() + "). Write the note.";
        return new TextResponse(callModel(system, userPrompt));
    }

    @Override
    public ProfileStrengthResponse getProfileStrength(String username) {
        UserProfile profile = userProfileRepository.findByAuthUser_Username(username)
                .or(() -> userProfileRepository.findByAuthUser_Email(username))
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found for: " + username));

        // Calculate score deterministically first, then ask AI for suggestions
        int score = 0;
        List<String> missing = new ArrayList<>();

        if (profile.getName() != null && !profile.getName().isBlank()) score += 10; else missing.add("full name");
        if (profile.getHeadLine() != null && !profile.getHeadLine().isBlank()) score += 15; else missing.add("professional headline");
        if (profile.getAvatarUrl() != null && !profile.getAvatarUrl().isBlank()) score += 15; else missing.add("profile photo");
        if (profile.getCoverageImageUrl() != null && !profile.getCoverageImageUrl().isBlank()) score += 5; else missing.add("cover image");
        if (profile.getLocation() != null && !profile.getLocation().isBlank()) score += 5; else missing.add("location");
        if (profile.getAbout() != null && !profile.getAbout().isBlank()) score += 20; else missing.add("About summary");
        if (!profile.getExperiences().isEmpty()) score += 15; else missing.add("work experience");
        if (!profile.getEducations().isEmpty()) score += 10; else missing.add("education");
        if (!profile.getSkills().isEmpty()) score += 5; else missing.add("skills");

        String level = score >= 80 ? "All-Star" : score >= 50 ? "Intermediate" : "Beginner";

        // Ask AI for natural-language suggestions
        String system = "You are a professional profile coach. Given a list of missing profile fields, return 3-5 short, motivating bullet-point tips to help the user improve their profile. Start each with a •.";
        String userPrompt = "Missing sections: " + String.join(", ", missing) + ". Profile score: " + score + "/100.";

        String raw = callModel(system, userPrompt);
        List<String> suggestions = Arrays.stream(raw.split("\n"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .limit(5)
                .collect(Collectors.toList());

        return new ProfileStrengthResponse(score, level, suggestions);
    }
}
