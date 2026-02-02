package com.flower.external.kakao;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class KakaoMapClient {

    private final WebClient webClient;

    public KakaoMapClient(@Value("${kakao.rest-api-key}") String apiKey) {
        this.webClient = WebClient.builder()
                .baseUrl("https://dapi.kakao.com")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "KakaoAK " + apiKey)
                .build();
    }

    public String searchKeyword(
            String keyword,
            double lng,
            double lat,
            int radius
    ) {

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v2/local/search/keyword.json")
                        .queryParam("query", keyword) // ✅ 인코딩 제거
                        .queryParam("x", lng)
                        .queryParam("y", lat)
                        .queryParam("radius", Math.min(radius, 20000))
                        .queryParam("sort", "distance")
                        .build()
                )
                .retrieve()
                .onStatus(
                        status -> status.isError(),
                        response -> response.bodyToMono(String.class)
                                .map(body ->
                                        new RuntimeException("❌ Kakao API Error: " + body)
                                )
                )
                .bodyToMono(String.class)
                .block();
    }
}
