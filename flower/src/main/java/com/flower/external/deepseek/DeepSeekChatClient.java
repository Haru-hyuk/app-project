package com.flower.external.deepseek;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class DeepSeekChatClient {

    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://api.deepseek.com")
            .build();

    @Value("${deepseek.api.key}")
    private String apiKey;

    public String generateMessage(String prompt) {
        Map<String, Object> body = Map.of(
                "model", "deepseek-chat",
                "messages", List.of(
                        Map.of("role", "user", "content", prompt)
                ),
                "temperature", 0.8
        );

        return webClient.post()
                .uri("/v1/chat/completions")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + apiKey)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> {
                    @SuppressWarnings("unchecked")
                    var choices = (List<Map<String, Object>>) response.get("choices");
                    @SuppressWarnings("unchecked")
                    var message = (Map<String, Object>) choices.get(0).get("message");
                    return message.get("content").toString().trim();
                })
                .block();
    }
}
