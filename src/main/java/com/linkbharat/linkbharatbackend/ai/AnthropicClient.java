package com.linkbharat.linkbharatbackend.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Profile("local")
@RequiredArgsConstructor
public class AnthropicClient implements AIModelClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${anthropic.api.key}")
    private String apiKey;

    private static final String URL = "https://api.anthropic.com/v1/messages";
    private static final String MODEL = "claude-haiku-4-5-20251001";

    @Override
    public String generate(String systemPrompt, String userPrompt) {

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("x-api-key", apiKey);
            headers.set("anthropic-version", "2023-06-01");

            Map<String,Object> message = Map.of("role","user", "content",userPrompt);

            Map<String,Object> body = new HashMap<>();
            body.put("model", MODEL);
            body.put("system", systemPrompt);
            body.put("max_tokens",512);
            body.put("messages", List.of(message));

            HttpEntity<Map<String,Object>> entity = new HttpEntity<>(body, headers);

            ResponseEntity<String> res = restTemplate.postForEntity(URL, entity, String.class);

            JsonNode root = objectMapper.readTree(res.getBody());
            return root.at("/content/0/text").asText("").trim();

        } catch(Exception e) {
            throw new RuntimeException("Anthropic error", e);
        }
    }
}
