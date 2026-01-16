package com.flower.controller;

import com.flower.dto.history.ViewHistoryResponse;
import com.flower.entity.User;
import com.flower.repository.UserRepository;
import com.flower.security.SecurityUtil;
import com.flower.service.ViewHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/view-history")
@RequiredArgsConstructor
public class ViewHistoryController {

    private final ViewHistoryService viewHistoryService;
    private final UserRepository userRepository;

    /**
     * 조회 기록 목록 조회
     */
    @GetMapping
    public ResponseEntity<List<ViewHistoryResponse>> getViewHistory() {
        Long userId = getCurrentUserId();
        return ResponseEntity.ok(viewHistoryService.getViewHistory(userId));
    }

    /**
     * 조회 기록 저장
     */
    @PostMapping
    public ResponseEntity<Map<String, String>> saveViewHistory(
            @RequestBody Map<String, Long> body
    ) {
        Long userId = getCurrentUserId();
        Long flowerId = body.get("flowerId");
        viewHistoryService.saveViewHistory(userId, flowerId);
        return ResponseEntity.ok(Map.of("message", "조회 기록이 저장되었습니다."));
    }

    /**
     * 특정 조회 기록 삭제
     */
    @DeleteMapping("/{viewId}")
    public ResponseEntity<Map<String, String>> deleteViewHistory(
            @PathVariable Long viewId
    ) {
        Long userId = getCurrentUserId();
        viewHistoryService.deleteViewHistory(userId, viewId);
        return ResponseEntity.ok(Map.of("message", "조회 기록이 삭제되었습니다."));
    }

    /**
     * 전체 조회 기록 삭제
     */
    @DeleteMapping
    public ResponseEntity<Map<String, String>> deleteAllViewHistory() {
        Long userId = getCurrentUserId();
        viewHistoryService.deleteAllViewHistory(userId);
        return ResponseEntity.ok(Map.of("message", "모든 조회 기록이 삭제되었습니다."));
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
