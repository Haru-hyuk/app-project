package com.flower.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class OllamaEmbeddingService implements EmbeddingService {

    private final WebClient webClient = WebClient.builder()
            .baseUrl("http://localhost:11434")
            .build();

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public float[] embed(String text) {
        try {
            String response = webClient.post()
                    .uri("/api/embeddings")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue("""
                        {
                          "model": "nomic-embed-text",
                          "prompt": "%s"
                        }
                        """.formatted(text))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            JsonNode root = objectMapper.readTree(response);
            JsonNode embeddingNode = root.get("embedding");

            float[] vector = new float[embeddingNode.size()];
            for (int i = 0; i < embeddingNode.size(); i++) {
                vector[i] = embeddingNode.get(i).floatValue();
            }

            return vector;

        } catch (Exception e) {
            throw new RuntimeException("Ollama embedding failed", e);
        }
    }
}
