package com.flower.controller;

import com.flower.dto.favorite.FavoriteResponse;
import com.flower.entity.User;
import com.flower.repository.UserRepository;
import com.flower.security.SecurityUtil;
import com.flower.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;
    private final UserRepository userRepository;

    /**
     * 즐겨찾기 목록 조회
     */
    @GetMapping
    public ResponseEntity<List<FavoriteResponse>> getFavorites() {
        Long userId = getCurrentUserId();
        return ResponseEntity.ok(favoriteService.getFavorites(userId));
    }

    /**
     * 즐겨찾기 추가
     */
    @PostMapping
    public ResponseEntity<FavoriteResponse> addFavorite(
            @RequestBody Map<String, Long> body
    ) {
        Long userId = getCurrentUserId();
        Long flowerId = body.get("flowerId");
        return ResponseEntity.ok(favoriteService.addFavorite(userId, flowerId));
    }

    /**
     * 즐겨찾기 삭제
     */
    @DeleteMapping("/{flowerId}")
    public ResponseEntity<Map<String, String>> removeFavorite(
            @PathVariable Long flowerId
    ) {
        Long userId = getCurrentUserId();
        favoriteService.removeFavorite(userId, flowerId);
        return ResponseEntity.ok(Map.of("message", "즐겨찾기에서 삭제되었습니다."));
    }

    /**
     * 즐겨찾기 여부 확인
     */
    @GetMapping("/check/{flowerId}")
    public ResponseEntity<Map<String, Object>> checkFavorite(
            @PathVariable Long flowerId
    ) {
        Long userId = getCurrentUserId();
        return ResponseEntity.ok(favoriteService.checkFavorite(userId, flowerId));
    }

    /**
     * 현재 로그인한 사용자 ID 조회
     */
    private Long getCurrentUserId() {
        String email = SecurityUtil.getCurrentUserEmail();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        return user.getUserId();
    }
}
