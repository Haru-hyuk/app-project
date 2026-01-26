package com.flower.controller;

import com.flower.service.FlowerEmbeddingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 임베딩 재생성 컨트롤러
 */
@RestController
@RequestMapping("/api/embedding")
@RequiredArgsConstructor
public class EmbeddingController {

    private final FlowerEmbeddingService flowerEmbeddingService;

    /**
     * 모든 꽃의 임베딩 재생성
     */
    @PostMapping("/regenerate-all")
    public ResponseEntity<Map<String, Object>> regenerateAllEmbeddings() {
        try {
            int count = flowerEmbeddingService.regenerateAllEmbeddings();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "임베딩 재생성이 완료되었습니다.");
            response.put("count", count);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "임베딩 재생성 중 오류가 발생했습니다: " + e.getMessage());
            
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 특정 꽃의 임베딩 재생성
     */
    @PostMapping("/regenerate/{flowerId}")
    public ResponseEntity<Map<String, Object>> regenerateFlowerEmbedding(@PathVariable Integer flowerId) {
        try {
            boolean success = flowerEmbeddingService.regenerateFlowerEmbedding(flowerId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", success);
            response.put("message", success ? "임베딩 재생성이 완료되었습니다." : "임베딩 재생성에 실패했습니다.");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "임베딩 재생성 중 오류가 발생했습니다: " + e.getMessage());
            
            return ResponseEntity.status(500).body(response);
        }
    }
}
