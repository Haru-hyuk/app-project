package com.flower.controller;

import com.flower.dto.flower.FlowerListResponse;
import com.flower.service.FlowerService;
import com.flower.service.KeywordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/keywords")
@RequiredArgsConstructor
public class KeywordController {

    private final KeywordService keywordService;
    private final FlowerService flowerService;

    /** 인기 검색 키워드 조회 */
    @GetMapping("/popular")
    public ResponseEntity<Map<String, Object>> getPopularKeywords() {
        List<String> keywords = keywordService.getPopularKeywords();
        return ResponseEntity.ok(Map.of(
                "keywords", keywords,
                "count", keywords.size()
        ));
    }

    /** 트렌딩 키워드 조회 (최근 7일간 인기 키워드) */
    @GetMapping("/trending")
    public ResponseEntity<Map<String, Object>> getTrendingKeywords() {
        List<String> keywords = keywordService.getTrendingKeywords();
        return ResponseEntity.ok(Map.of(
                "keywords", keywords,
                "count", keywords.size()
        ));
    }

    /**
     * 인기 키워드 클릭 시 꽃 검색 (임베딩 없이 DB 직접 검색)
     * - 꽃말(floriography)과 키워드(flowerKeyword)에서 직접 검색
     * - AI 임베딩을 사용하지 않아 빠름
     */
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchByKeyword(@RequestParam String keyword) {
        // 인기 키워드 카운트 증가
        keywordService.incrementKeywordCount(keyword);

        // DB에서 꽃말/키워드로 직접 검색 (임베딩 사용 안함)
        List<FlowerListResponse> results = flowerService.searchFlowersByFloriography(keyword);

        return ResponseEntity.ok(Map.of(
                "results", results,
                "count", results.size(),
                "keyword", keyword
        ));
    }
}
