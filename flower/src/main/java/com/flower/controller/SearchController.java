package com.flower.controller;

import com.flower.dto.flower.FlowerSearchResult;
import com.flower.entity.User;
import com.flower.repository.UserRepository;
import com.flower.security.SecurityUtil;
import com.flower.service.KeywordService;
import com.flower.service.SearchHistoryService;
import com.flower.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/search")
public class SearchController {

    private final SearchHistoryService searchHistoryService;
    private final SearchService searchService;
    private final KeywordService keywordService;
    private final UserRepository userRepository;

    /**
     * 벡터 유사도 기반 꽃 검색
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> search(@RequestParam String query) {

        String email = SecurityUtil.getCurrentUserEmail();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 최근 검색어 저장
        searchHistoryService.save(user.getUserId(), query);

        // 인기 키워드 카운트 증가
        keywordService.incrementKeywordCount(query);

        // 벡터 유사도 기반 검색 실행
        List<FlowerSearchResult> results = searchService.search(query);

        return ResponseEntity.ok(Map.of(
                "results", results,
                "count", results.size(),
                "query", query
        ));
    }

    @GetMapping("/recent")
    public List<String> recent() {

        String email = SecurityUtil.getCurrentUserEmail();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        return searchHistoryService.getRecent(user.getUserId());
    }

    @DeleteMapping("/recent")
    public void deleteOne(@RequestParam String query) {

        String email = SecurityUtil.getCurrentUserEmail();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        searchHistoryService.deleteOne(user.getUserId(), query);
    }

    @DeleteMapping("/recent/all")
    public void deleteAll() {

        String email = SecurityUtil.getCurrentUserEmail();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        searchHistoryService.deleteAll(user.getUserId());
    }
}
