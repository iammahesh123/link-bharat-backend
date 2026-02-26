package com.linkbharat.linkbharatbackend.controller;

import com.linkbharat.linkbharatbackend.domain.model.ai.*;
import com.linkbharat.linkbharatbackend.service.AIService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
@Tag(name = "AI Features", description = "AI-powered messaging assistant and smart networking APIs backed by Anthropic Claude.")
@SecurityRequirement(name = "bearerAuth")
public class AIController {

    private final AIService aiService;

    // ── Messaging AI ─────────────────────────────────────────────────────────

    @Operation(summary = "Get smart reply suggestions",
            description = "Given recent conversation messages, returns 3 short AI-generated quick-reply options.")
    @PostMapping("/messaging/smart-replies")
    public ResponseEntity<SmartReplyResponse> getSmartReplies(
            @RequestBody SmartReplyRequest request) {
        return ResponseEntity.ok(aiService.getSmartReplies(request));
    }

    @Operation(summary = "Rewrite a message in a given tone",
            description = "Rewrites the user's draft message in the requested tone (professional, friendly, concise).")
    @PostMapping("/messaging/rewrite")
    public ResponseEntity<TextResponse> rewriteMessage(
            @RequestBody RewriteRequest request) {
        return ResponseEntity.ok(aiService.rewriteMessage(request));
    }

    @Operation(summary = "Auto-complete a partial message",
            description = "Completes the user's partial message naturally based on the conversation context.")
    @PostMapping("/messaging/autocomplete")
    public ResponseEntity<TextResponse> autoComplete(
            @RequestBody AutoCompleteRequest request) {
        return ResponseEntity.ok(aiService.autoComplete(request));
    }

    @Operation(summary = "Summarize a conversation",
            description = "Produces a 3-5 bullet-point summary of a conversation.")
    @PostMapping("/messaging/summarize")
    public ResponseEntity<TextResponse> summarize(
            @RequestBody SummaryRequest request) {
        return ResponseEntity.ok(aiService.summarizeConversation(request));
    }

    // ── Networking AI ─────────────────────────────────────────────────────────

    @Operation(summary = "Explain why two users should connect",
            description = "Returns a 1-2 sentence recommendation explaining the networking value for the current user.")
    @PostMapping("/networking/recommendation")
    public ResponseEntity<TextResponse> getRecommendation(
            @RequestBody ConnectionRecommendationRequest request) {
        return ResponseEntity.ok(aiService.getConnectionRecommendation(request));
    }

    @Operation(summary = "Generate a personalised connection request note",
            description = "Creates a warm, concise connection message tailored to the recipient's profile.")
    @PostMapping("/networking/connection-message")
    public ResponseEntity<TextResponse> generateConnectionMessage(
            @RequestBody ConnectionMessageRequest request) {
        return ResponseEntity.ok(aiService.generateConnectionMessage(request));
    }

    @Operation(summary = "Get the current user's AI profile-strength score",
            description = "Returns a 0-100 score, a level label, and AI-generated suggestions to improve the profile.")
    @GetMapping("/networking/profile-strength")
    public ResponseEntity<ProfileStrengthResponse> getProfileStrength(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(aiService.getProfileStrength(userDetails.getUsername()));
    }
}
