package com.flower.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flower.search.dto.DeepSeekChatRequest;
import com.flower.search.dto.DeepSeekChatResponse;
import com.flower.search.dto.SemanticIntent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DeepSeekChatService {

    @Value("${deepseek.api.key}")
    private String apiKey;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String SYSTEM_PROMPT = """
        너는 꽃 추천 서비스의 의미 분석기다.
        추천하거나 판단하지 말고, 의미만 구조화하라.

        출력은 반드시 JSON 형식만 반환하라.

        형식:
        {
          "emotion": [],
          "tone": [],
          "occasion": [],
          "target": [],
          "exclude": []
        }
        """;

    public SemanticIntent analyze(String query) {

        WebClient webClient = WebClient.builder()
                .baseUrl("https://api.deepseek.com/v1/chat/completions")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();

        DeepSeekChatRequest request = new DeepSeekChatRequest(
                "deepseek-chat",
                List.of(
                        Map.of("role", "system", "content", SYSTEM_PROMPT),
                        Map.of("role", "user", "content", query)
                ),
                0.2
        );

        DeepSeekChatResponse response = webClient.post()
                .bodyValue(request)
                .retrieve()
                .bodyToMono(DeepSeekChatResponse.class)
                .block();

        if (response == null || response.getChoices().isEmpty()) {
            throw new RuntimeException("DeepSeek 응답이 비어있습니다.");
        }

        String json = response.getChoices().get(0).getMessage().getContent();

        try {
            return objectMapper.readValue(json, SemanticIntent.class);
        } catch (Exception e) {
            throw new RuntimeException("DeepSeek JSON 파싱 실패: " + json, e);
        }
    }
}
