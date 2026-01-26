package com.flower.dto.message;

import lombok.Getter;

import java.util.List;

@Getter
public class CardMessageRequest {

    private Integer flowerId;           // 꽃 ID (DB 저장용)
    private String flowerName;          // 선택한 꽃 이름
    private List<String> floriography;  // 꽃말
    private String query;               // 검색어 / 상황 (ex. 졸업 축하)
}
