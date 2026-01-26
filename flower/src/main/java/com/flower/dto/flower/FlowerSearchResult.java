package com.flower.dto.flower;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 벡터 유사도 검색 결과 DTO
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FlowerSearchResult {
    private FlowerListResponse flower;
    private Double similarity;  // 유사도 점수 (0.0 ~ 1.0)
}
