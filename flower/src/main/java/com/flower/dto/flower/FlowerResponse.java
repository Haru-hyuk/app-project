package com.flower.dto.flower;

import com.flower.entity.Flower;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

// 단건 상세 조회 응답 DTO (상세 설명, 오늘의 꽃, 즐겨찾기 여부 포함)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlowerResponse {

    private Long flowerId;
    private String flowerName;
    private List<String> floriography;
    private List<String> flowerKeyword;
    private String flowerOrigin;
    private String flowerDescribe;
    private String imageUrl;
    private List<String> season;
    private LocalDate todayFlower;
    private Boolean isFavorite;

    public static FlowerResponse from(Flower flower) {
        return FlowerResponse.builder()
                .flowerId(flower.getFlowerId())
                .flowerName(flower.getFlowerName())
                .floriography(flower.getFloriography())
                .flowerKeyword(flower.getFlowerKeyword())
                .flowerOrigin(flower.getFlowerOrigin())
                .flowerDescribe(flower.getFlowerDescribe())
                .imageUrl(flower.getImageUrl())
                .season(flower.getSeason())
                .todayFlower(flower.getTodayFlower())
                .isFavorite(false)
                .build();
    }

    public static FlowerResponse from(Flower flower, boolean isFavorite) {
        FlowerResponse response = from(flower);
        response.setIsFavorite(isFavorite);
        return response;
    }
}
