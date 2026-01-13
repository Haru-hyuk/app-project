package com.flower.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flower.dto.shop.ShopResponse;
import com.flower.external.kakao.KakaoMapClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ShopService {

	private final KakaoMapClient kakaoMapClient;
	private final ObjectMapper objectMapper;

    public List<ShopResponse> findNearbyShops(
            double lat,
            double lng,
            int radius,
            String keyword
    ) {
        try {
            String response = kakaoMapClient.searchKeyword(keyword, lng, lat, radius);
            JsonNode root = objectMapper.readTree(response);
            JsonNode documents = root.get("documents");

            List<ShopResponse> result = new ArrayList<>();

            for (JsonNode doc : documents) {
                result.add(
                        ShopResponse.builder()
                                .name(doc.get("place_name").asText())
                                .address(
                                	    doc.hasNonNull("road_address_name")
                                	        ? doc.get("road_address_name").asText()
                                	        : doc.get("address_name").asText("")
                                	)
                                .lat(doc.get("y").asDouble())
                                .lng(doc.get("x").asDouble())
                                .distance(doc.get("distance").asInt())
                                .phone(doc.get("phone").asText(""))
                                .placeUrl(doc.get("place_url").asText(""))
                                .build()
                );
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace(); // 🔥 콘솔에 진짜 원인 찍기
            throw new RuntimeException(e.getMessage());
        }
    }
}
