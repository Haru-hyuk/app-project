package com.flower.dto.flower;

import com.flower.entity.Flower;
import lombok.*;

import java.util.List;

// 목록/검색 응답에서 사용하는 요약 DTO
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlowerListResponse {

    private Integer flowerId;
    private String flowerName;
    private List<String> floriography;
    private List<String> flowerKeyword;
    private String imageUrl;
    private List<String> season;

    public static FlowerListResponse from(Flower flower) {
        return FlowerListResponse.builder()
                .flowerId(flower.getFlowerId())
                .flowerName(flower.getFlowerName())
                .floriography(flower.getFloriography())
                .flowerKeyword(flower.getFlowerKeyword())
                .imageUrl(flower.getImageUrl())
                .season(flower.getSeason())
                .build();
    }
}
