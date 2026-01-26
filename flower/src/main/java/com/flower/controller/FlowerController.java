package com.flower.controller;

import com.flower.dto.flower.FlowerListResponse;
import com.flower.dto.flower.FlowerResponse;
import com.flower.entity.User;
import com.flower.repository.UserRepository;
import com.flower.security.SecurityUtil;
import com.flower.service.FlowerService;
import com.flower.service.KeywordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/flowers")
@RequiredArgsConstructor
public class FlowerController {

    private final FlowerService flowerService;
    private final UserRepository userRepository;
    private final KeywordService keywordService;

    /**
     * 전체 꽃 목록 조회
     */
    @GetMapping
    public ResponseEntity<List<FlowerListResponse>> getAllFlowers() {
        return ResponseEntity.ok(flowerService.getAllFlowers());
    }

    /**
     * 오늘의 꽃 조회
     */
    @GetMapping("/today")
    public ResponseEntity<FlowerResponse> getTodayFlower() {
        FlowerResponse flower = flowerService.getTodayFlower();
        if (flower == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(flower);
    }

    /**
     * 계절별 꽃 목록 조회
     */
    @GetMapping("/season")
    public ResponseEntity<List<FlowerListResponse>> getFlowersBySeason(
            @RequestParam String season
    ) {
        return ResponseEntity.ok(flowerService.getFlowersBySeason(season));
    }

    /**
     * 계절별 랜덤 꽃 1개 조회 (최적화)
     */
    @GetMapping("/season/random")
    public ResponseEntity<FlowerListResponse> getRandomFlowerBySeason(
            @RequestParam String season
    ) {
        FlowerListResponse flower = flowerService.getRandomFlowerBySeason(season);
        if (flower == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(flower);
    }

    /**
     * 모든 계절의 랜덤 꽃 한 번에 조회 (4번 호출 → 1번 호출로 최적화)
     */
    @GetMapping("/season/random/all")
    public ResponseEntity<java.util.Map<String, FlowerListResponse>> getRandomFlowersForAllSeasons() {
        return ResponseEntity.ok(flowerService.getRandomFlowersForAllSeasons());
    }

    /**
     * 꽃 상세 정보 조회
     */
    @GetMapping("/{flowerId}")
    public ResponseEntity<FlowerResponse> getFlowerDetail(
            @PathVariable Integer flowerId
    ) {
        Integer userId = getCurrentUserIdOrNull();
        return ResponseEntity.ok(flowerService.getFlowerDetail(flowerId, userId));
    }

    /**
     * 키워드로 꽃 검색
     */
    @GetMapping("/keyword")
    public ResponseEntity<List<FlowerListResponse>> getFlowersByKeyword(
            @RequestParam String keyword
    ) {
        return ResponseEntity.ok(flowerService.getFlowersByKeyword(keyword));
    }

    /**
     * 꽃 이름으로 검색
     */
    @GetMapping("/search")
    public ResponseEntity<List<FlowerListResponse>> searchFlowers(
            @RequestParam String name
    ) {
        return ResponseEntity.ok(flowerService.searchFlowersByName(name));
    }

    /**
     * 꽃말/키워드로 검색 (검색 결과가 있으면 인기 키워드 카운트 증가)
     */
    @GetMapping("/search/floriography")
    public ResponseEntity<List<FlowerListResponse>> searchByFloriography(
            @RequestParam String query
    ) {
        List<FlowerListResponse> results = flowerService.searchFlowersByFloriography(query);

        // 검색 결과가 있으면 인기 키워드 카운트 증가
        if (!results.isEmpty()) {
            keywordService.incrementKeywordCount(query);
        }

        return ResponseEntity.ok(results);
    }

    /**
     * 현재 로그인한 사용자 ID 조회 (없으면 null)
     */
    private Integer getCurrentUserIdOrNull() {
        try {
            String email = SecurityUtil.getCurrentUserEmail();
            if (email == null || email.equals("anonymousUser")) {
                return null;
            }
            return userRepository.findByEmail(email)
                    .map(User::getUserId)
                    .orElse(null);
        } catch (Exception e) {
            return null;
        }
    }
}
