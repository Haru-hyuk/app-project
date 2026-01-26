package com.flower.external.kakao;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class KakaoAuthClient {

    private final WebClient webClient;

    public KakaoAuthClient() {
        this.webClient = WebClient.builder()
                .baseUrl("https://kapi.kakao.com")
                .build();
    }

    public KakaoUserInfo getUserInfo(String accessToken) {
        return webClient.get()
                .uri("/v2/user/me")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .onStatus(
                        HttpStatusCode::isError,
                        response -> response.bodyToMono(String.class)
                                .map(body -> new RuntimeException("Kakao auth error: " + body))
                )
                .bodyToMono(KakaoUserInfo.class)
                .block();
    }
}
