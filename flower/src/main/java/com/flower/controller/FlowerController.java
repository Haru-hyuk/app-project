package com.flower.controller;

import com.flower.dto.flower.FlowerListResponse;
import com.flower.dto.flower.FlowerResponse;
import com.flower.entity.User;
import com.flower.repository.UserRepository;
import com.flower.security.SecurityUtil;
import com.flower.service.FlowerService;
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
     * 꽃 상세 정보 조회
     */
    @GetMapping("/{flowerId}")
    public ResponseEntity<FlowerResponse> getFlowerDetail(
            @PathVariable Long flowerId
    ) {
        Long userId = getCurrentUserIdOrNull();
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
     * 현재 로그인한 사용자 ID 조회 (없으면 null)
     */
    private Long getCurrentUserIdOrNull() {
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
