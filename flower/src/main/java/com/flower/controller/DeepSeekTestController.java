package com.flower.controller;

import com.flower.search.dto.SemanticIntent;
import com.flower.service.DeepSeekChatService;
import com.flower.service.EmbeddingService;
import com.flower.service.SemanticQueryBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/test/deepseek")
public class DeepSeekTestController {

    private final DeepSeekChatService deepSeekChatService;
    private final SemanticQueryBuilder semanticQueryBuilder;
    private final EmbeddingService embeddingService;

    /**
     * 1️⃣ 의미 해석 테스트
     */
    @PostMapping
    public SemanticIntent analyze(@RequestParam("query") String query) {
        return deepSeekChatService.analyze(query);
    }

    /**
     * 2️⃣ 의미 문장 + 임베딩 테스트
     */
    @PostMapping("/embedding")
    public Map<String, Object> embedding(@RequestParam("query") String query) {

        // 1. 의미 해석
        SemanticIntent intent = deepSeekChatService.analyze(query);

        // 2. 의미 문장 생성
        String semanticQuery = semanticQueryBuilder.build(intent);

        // 3. 임베딩 (float[])
        float[] vector = embeddingService.embed(semanticQuery);

        Map<String, Object> result = new HashMap<>();
        result.put("semanticQuery", semanticQuery);
        result.put("vectorLength", vector.length);

        // 앞부분 미리보기
        int previewSize = Math.min(20, vector.length);
        float[] preview = new float[previewSize];
        System.arraycopy(vector, 0, preview, 0, previewSize);

        result.put("vectorPreview", preview);

        return result;
    }
}