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

import java.util.List;
import java.util.Map;

@Service
@Profile("dev")
@RequiredArgsConstructor
public class GeminiClient implements AIModelClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${gemini.api.key}")
    private String apiKey;

    private static final String URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent";
    @Override
    public String generate(String systemPrompt, String userPrompt) {

        try {
            String fullPrompt = systemPrompt + "\n\n" + userPrompt;

            Map<String,Object> part = Map.of("text", fullPrompt);
            Map<String,Object> content = Map.of("parts", List.of(part));
            Map<String,Object> body = Map.of("contents", List.of(content));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String,Object>> entity = new HttpEntity<>(body, headers);

            ResponseEntity<String> res = restTemplate.postForEntity(URL + "?key=" + apiKey, entity, String.class);

            JsonNode root = objectMapper.readTree(res.getBody());

            return root.at("/candidates/0/content/parts/0/text")
                    .asText("")
                    .trim();

        } catch(Exception e) {
            throw new RuntimeException("Gemini error", e);
        }
    }
}