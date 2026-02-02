package com.flower.service;

import com.flower.dto.flower.FlowerListResponse;
import com.flower.dto.flower.FlowerSearchResult;
import com.flower.entity.Flower;
import com.flower.repository.FlowerRepository;
import com.flower.search.dto.SemanticIntent;
import com.flower.util.VectorSimilarityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final DeepSeekChatService deepSeekChatService;
    private final SemanticQueryBuilder semanticQueryBuilder;
    private final EmbeddingService embeddingService;
    private final FlowerRepository flowerRepository;

    private static final int TOP_K_CANDIDATES = 30;     // 1차 유사도 상위 후보 수
    private static final int FINAL_RESULTS = 5;          // 최종 반환 결과 수
    private static final double MIN_SIMILARITY = 0.0;    // 최소 유사도 (필요시 조정)

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

        // 후보 꽃이 적으면 항상 비슷한 추천만 나옴 (원인 확인용 로그)
        if (flowersWithMatchingDimension < 20) {
            // SLF4J/Logback 사용 시: log.warn("시멘틱 검색 후보 꽃 수가 적음: {} (전체 꽃 중 임베딩 있음: {}) → POST /api/embedding/regenerate-all 로 전체 임베딩 생성 필요", flowersWithMatchingDimension, flowersWithEmbedding);
            System.err.println("[시멘틱 검색] 후보 꽃 수: " + flowersWithMatchingDimension + ", 임베딩 있는 꽃: " + flowersWithEmbedding + " → 같은 추천만 나오면 POST /api/embedding/regenerate-all 실행 필요");
        }

        System.out.println("[검색 디버그] 전체 꽃: " + allFlowers.size()
                + ", 임베딩 있는 꽃: " + flowersWithEmbedding
                + ", 차원 일치 꽃: " + flowersWithMatchingDimension
                + ", 쿼리 차원: " + queryEmbeddingDimension
                + ", 꽃 차원: " + firstFlowerDimension);

        // 5. 벡터 유사도 계산 → 상위 30개 후보 선정 (차원이 일치하는 꽃만)
        List<FlowerSearchResult> topKCandidates = allFlowers.stream()
                .filter(flower -> {
                    if (flower.getEmbedding() == null || flower.getEmbedding().isEmpty()) {
                        return false;
                    }
                    return flower.getEmbedding().size() == queryEmbeddingDimension;
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
                .limit(TOP_K_CANDIDATES)
                .collect(Collectors.toList());

        System.out.println("[검색 디버그] top-30 후보 수: " + topKCandidates.size()
                + ", 셔플 전: " + topKCandidates.stream()
                    .map(r -> r.getFlower().getFlowerName())
                    .collect(Collectors.joining(", ")));

        // 6. 상위 30개를 완전 랜덤 셔플 후 5개 선택
        Collections.shuffle(topKCandidates);

        List<FlowerSearchResult> results = topKCandidates.stream()
                .limit(FINAL_RESULTS)
                .collect(Collectors.toList());

        System.out.println("[검색 디버그] 셔플 후 최종 5개: " + results.stream()
                .map(r -> r.getFlower().getFlowerName() + "(" + String.format("%.4f", r.getSimilarity()) + ")")
                .collect(Collectors.joining(", ")));

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
