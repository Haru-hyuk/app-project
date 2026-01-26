package com.flower.service;

import com.flower.entity.Flower;
import com.flower.repository.FlowerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 꽃 임베딩 재생성 서비스
 */
@Service
@RequiredArgsConstructor
public class FlowerEmbeddingService {

    private final FlowerRepository flowerRepository;
    private final EmbeddingService embeddingService;

    /**
     * 모든 꽃의 임베딩을 재생성
     * @return 재생성된 꽃 개수
     */
    @Transactional
    public int regenerateAllEmbeddings() {
        List<Flower> allFlowers = flowerRepository.findAll();

        int successCount = 0;
        int failCount = 0;

        for (Flower flower : allFlowers) {
            try {
                // 꽃 정보를 텍스트로 조합
                String text = buildFlowerText(flower);
                
                // 임베딩 생성
                float[] embedding = embeddingService.embed(text);
                
                // List<Double>로 변환
                List<Double> embeddingList = new ArrayList<>();
                for (float value : embedding) {
                    embeddingList.add((double) value);
                }
                
                // 임베딩 업데이트
                flower = Flower.builder()
                        .flowerId(flower.getFlowerId())
                        .flowerName(flower.getFlowerName())
                        .floriography(flower.getFloriography())
                        .flowerKeyword(flower.getFlowerKeyword())
                        .flowerOrigin(flower.getFlowerOrigin())
                        .flowerDescribe(flower.getFlowerDescribe())
                        .imageUrl(flower.getImageUrl())
                        .season(flower.getSeason())
                        .todayFlower(flower.getTodayFlower())
                        .embedding(embeddingList)
                        .build();
                
                flowerRepository.save(flower);
                successCount++;
                
            } catch (Exception e) {
                failCount++;
            }
        }

        return successCount;
    }

    /**
     * 특정 꽃의 임베딩만 재생성
     */
    @Transactional
    public boolean regenerateFlowerEmbedding(Integer flowerId) {
        Flower flower = flowerRepository.findById(flowerId)
                .orElseThrow(() -> new RuntimeException("꽃을 찾을 수 없습니다: " + flowerId));

        try {
            String text = buildFlowerText(flower);
            float[] embedding = embeddingService.embed(text);
            
            List<Double> embeddingList = new ArrayList<>();
            for (float value : embedding) {
                embeddingList.add((double) value);
            }
            
            flower = Flower.builder()
                    .flowerId(flower.getFlowerId())
                    .flowerName(flower.getFlowerName())
                    .floriography(flower.getFloriography())
                    .flowerKeyword(flower.getFlowerKeyword())
                    .flowerOrigin(flower.getFlowerOrigin())
                    .flowerDescribe(flower.getFlowerDescribe())
                    .imageUrl(flower.getImageUrl())
                    .season(flower.getSeason())
                    .todayFlower(flower.getTodayFlower())
                    .embedding(embeddingList)
                    .build();
            
            flowerRepository.save(flower);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 꽃 정보를 텍스트로 조합 (임베딩 생성용)
     * 꽃 이름, 꽃말, 키워드, 계절 사용
     */
    private String buildFlowerText(Flower flower) {
        StringBuilder text = new StringBuilder();
        
        // 꽃 이름
        text.append(flower.getFlowerName()).append(" ");
        
        // 꽃말
        if (flower.getFloriography() != null && !flower.getFloriography().isEmpty()) {
            text.append(String.join(", ", flower.getFloriography())).append(" ");
        }
        
        // 키워드
        if (flower.getFlowerKeyword() != null && !flower.getFlowerKeyword().isEmpty()) {
            text.append(String.join(", ", flower.getFlowerKeyword())).append(" ");
        }
        
        // 계절
        if (flower.getSeason() != null && !flower.getSeason().isEmpty()) {
            text.append(String.join(", ", flower.getSeason()));
        }
        
        return text.toString().trim();
    }
}
