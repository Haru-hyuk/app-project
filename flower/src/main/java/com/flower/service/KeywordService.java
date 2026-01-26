package com.flower.service;

import com.flower.entity.PopularKeyword;
import com.flower.repository.PopularKeywordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KeywordService {

    private final PopularKeywordRepository repository;
    private static final int MAX_POPULAR_KEYWORDS = 10;
    private static final int MAX_TRENDING_KEYWORDS = 10;

    /** 인기 검색 키워드 조회 */
    public List<String> getPopularKeywords() {
        return repository.findPopularKeywords()
                .stream()
                .limit(MAX_POPULAR_KEYWORDS)
                .map(PopularKeyword::getKeyword)
                .collect(Collectors.toList());
    }

    /** 트렌딩 키워드 조회 (최근 7일간 인기 키워드) */
    public List<String> getTrendingKeywords() {
        List<PopularKeyword> trending = repository.findTrendingKeywords();
        
        // 최근 7일간 데이터가 없으면 전체 인기 키워드 반환
        if (trending.isEmpty()) {
            return getPopularKeywords();
        }
        
        return trending.stream()
                .limit(MAX_TRENDING_KEYWORDS)
                .map(PopularKeyword::getKeyword)
                .collect(Collectors.toList());
    }

    /** 키워드 검색 횟수 증가 (검색 시 호출) */
    @Transactional
    public void incrementKeywordCount(String keyword) {
        repository.findByKeyword(keyword)
                .ifPresentOrElse(
                        PopularKeyword::incrementCount,
                        () -> repository.save(
                                PopularKeyword.builder()
                                        .keyword(keyword)
                                        .count(1)
                                        .build()
                        )
                );
    }
}
