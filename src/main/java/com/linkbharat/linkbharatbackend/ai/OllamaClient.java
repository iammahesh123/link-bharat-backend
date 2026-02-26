package com.linkbharat.linkbharatbackend.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@Profile("dev")
@RequiredArgsConstructor
public class OllamaClient implements AIModelClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    private static final String URL = "http://localhost:11434/api/generate";
    private static final String MODEL = "llama3";

    @Override
    public String generate(String systemPrompt, String userPrompt) {

        try {
            Map<String,Object> body = new HashMap<>();
            body.put("model", MODEL);
            body.put("prompt", systemPrompt + "\n\n" + userPrompt);
            body.put("stream", false);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String,Object>> entity = new HttpEntity<>(body, headers);

            ResponseEntity<String> res = restTemplate.postForEntity(URL, entity, String.class);

            JsonNode root = objectMapper.readTree(res.getBody());

            return root.path("response").asText("").trim();

        } catch(Exception e) {
            throw new RuntimeException("Ollama error", e);
        }
    }
}
