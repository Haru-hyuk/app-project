package com.flower.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

import jakarta.annotation.PostConstruct;
import java.time.Duration;

@Service
@RequiredArgsConstructor
public class OllamaEmbeddingService implements EmbeddingService {

    @Value("${ollama.base-url:http://localhost:11434}")
    private String baseUrl;

    @Value("${ollama.model:nomic-embed-text}")
    private String model;

    @Value("${ollama.timeout:30}")
    private int timeoutSeconds;

    private WebClient webClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostConstruct
    public void init() {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(2 * 1024 * 1024)) // 2MB
                .build();
    }

    @Override
    public float[] embed(String text) {
        try {
            String requestBody = String.format("""
                {
                  "model": "%s",
                  "prompt": "%s"
                }
                """, model, text.replace("\"", "\\\""));

            String response = webClient.post()
                    .uri("/api/embeddings")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(timeoutSeconds))
                    .retryWhen(Retry.fixedDelay(2, Duration.ofSeconds(1))) // 2회 재시도
                    .block();

            if (response == null || response.isEmpty()) {
                throw new RuntimeException("Ollama returned empty response");
            }

            JsonNode root = objectMapper.readTree(response);
            JsonNode embeddingNode = root.get("embedding");

            if (embeddingNode == null || !embeddingNode.isArray()) {
                throw new RuntimeException("Invalid embedding response format");
            }

            float[] vector = new float[embeddingNode.size()];
            for (int i = 0; i < embeddingNode.size(); i++) {
                vector[i] = embeddingNode.get(i).floatValue();
            }

            return vector;

        } catch (Exception e) {
            throw new RuntimeException("Ollama embedding failed: " + e.getMessage(), e);
        }
    }
}
