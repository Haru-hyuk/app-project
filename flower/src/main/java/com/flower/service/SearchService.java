package com.flower.service;

import com.flower.dto.flower.FlowerListResponse;
import com.flower.dto.flower.FlowerSearchResult;
import com.flower.entity.Flower;
import com.flower.repository.FlowerRepository;
import com.flower.search.dto.SemanticIntent;
import com.flower.util.VectorSimilarityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final DeepSeekChatService deepSeekChatService;
    private final SemanticQueryBuilder semanticQueryBuilder;
    private final EmbeddingService embeddingService;
    private final FlowerRepository flowerRepository;

    private static final int MAX_SEARCH_RESULTS = 5;  // 최대 검색 결과 수 (유사도 top 5)
    private static final double MIN_SIMILARITY = 0.0;  // 최소 유사도 (필요시 조정)

    /**
     * 벡터 유사도 기반 꽃 검색 (폴백 없음)
     * @param query 사용자 검색어
     * @return 유사도가 높은 순으로 정렬된 꽃 목록
     * @throws RuntimeException 임베딩 생성 실패 시
     */
    public List<FlowerSearchResult> search(String query) {
        // 1. 의미 해석
        SemanticIntent intent = deepSeekChatService.analyze(query);

        // 2. 의미 문장 생성
        String semanticQuery = semanticQueryBuilder.build(intent);

        // 3. 임베딩 벡터 생성 (실패 시 예외 발생)
        float[] queryEmbedding = embeddingService.embed(semanticQuery);
        final int queryEmbeddingDimension = queryEmbedding.length;

        // 4. 모든 꽃 조회
        List<Flower> allFlowers = flowerRepository.findAll();

        // 임베딩 통계 수집 및 차원 확인
        long flowersWithEmbedding = 0;
        long flowersWithMatchingDimension = 0;
        int firstFlowerDimension = -1;
        
        for (Flower flower : allFlowers) {
            if (flower.getEmbedding() != null && !flower.getEmbedding().isEmpty()) {
                flowersWithEmbedding++;
                int flowerDimension = flower.getEmbedding().size();
                if (firstFlowerDimension == -1) {
                    firstFlowerDimension = flowerDimension;
                }
                if (flowerDimension == queryEmbeddingDimension) {
                    flowersWithMatchingDimension++;
                }
            }
        }
        
        if (flowersWithMatchingDimension == 0) {
            throw new RuntimeException("차원 불일치: 쿼리 임베딩(" + queryEmbeddingDimension + "차원)과 꽃 임베딩(" + firstFlowerDimension + "차원)이 일치하지 않습니다.");
        }

        // exclude에 포함된 부정적인 키워드 목록
        Set<String> excludeKeywords = new HashSet<>();
        if (intent.getExclude() != null) {
            intent.getExclude().forEach(e -> excludeKeywords.add(e.trim().toLowerCase()));
        }
        
        // 부정적인 꽃말 키워드 (exclude에 자동 추가)
        Set<String> negativeKeywords = Set.of(
            "절망", "이루어지지 않는", "이루어지지않는", "이루어지지 않는 사랑",
            "사랑의 절망", "이별", "슬픔", "고통", "비애", "절망감"
        );
        excludeKeywords.addAll(negativeKeywords);

        // 5. 벡터 유사도 계산 및 정렬 (차원이 일치하는 꽃만)
        List<FlowerSearchResult> results = allFlowers.stream()
                .filter(flower -> {
                    if (flower.getEmbedding() == null || flower.getEmbedding().isEmpty()) {
                        return false;
                    }
                    // 차원이 일치하는 꽃만 필터링
                    if (flower.getEmbedding().size() != queryEmbeddingDimension) {
                        return false;
                    }
                    
                    // 부정적인 꽃말/키워드를 가진 꽃 필터링
                    if (flower.getFloriography() != null) {
                        for (String floriography : flower.getFloriography()) {
                            String lowerFloriography = floriography.toLowerCase();
                            for (String exclude : excludeKeywords) {
                                if (lowerFloriography.contains(exclude)) {
                                    return false;
                                }
                            }
                        }
                    }
                    if (flower.getFlowerKeyword() != null) {
                        for (String keyword : flower.getFlowerKeyword()) {
                            String lowerKeyword = keyword.toLowerCase();
                            for (String exclude : excludeKeywords) {
                                if (lowerKeyword.contains(exclude)) {
                                    return false;
                                }
                            }
                        }
                    }
                    
                    return true;
                })
                .map(flower -> {
                    double similarity = VectorSimilarityUtil.cosineSimilarity(
                            queryEmbedding,
                            flower.getEmbedding()
                    );
                    return FlowerSearchResult.builder()
                            .flower(FlowerListResponse.from(flower))
                            .similarity(similarity)
                            .build();
                })
                .filter(result -> result.getSimilarity() >= MIN_SIMILARITY)
                .sorted(Comparator.comparing(FlowerSearchResult::getSimilarity).reversed())
                .limit(MAX_SEARCH_RESULTS)
                .collect(Collectors.toList());

        return results;
    }

    /**
     * 임베딩 벡터만 반환 (기존 메서드 - 호환성 유지)
     * @param query 사용자 검색어
     * @return 임베딩 벡터
     */
    public float[] getEmbedding(String query) {
        SemanticIntent intent = deepSeekChatService.analyze(query);
        String semanticQuery = semanticQueryBuilder.build(intent);
        return embeddingService.embed(semanticQuery);
    }
}
