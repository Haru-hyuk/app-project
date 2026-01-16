package com.flower.service;

import com.flower.dto.flower.FlowerListResponse;
import com.flower.dto.flower.FlowerResponse;
import com.flower.entity.Flower;
import com.flower.repository.FavoriteRepository;
import com.flower.repository.FlowerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FlowerService {

    private final FlowerRepository flowerRepository;
    private final FavoriteRepository favoriteRepository;

    /**
     * 전체 꽃 목록 조회
     */
    public List<FlowerListResponse> getAllFlowers() {
        return flowerRepository.findAllByOrderByFlowerIdDesc()
                .stream()
                .map(FlowerListResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 오늘의 꽃 조회
     */
    public FlowerResponse getTodayFlower() {
        LocalDate today = LocalDate.now();
        return flowerRepository.findByTodayFlower(today)
                .map(FlowerResponse::from)
                .orElse(null);
    }

    /**
     * 계절별 꽃 목록 조회
     */
    public List<FlowerListResponse> getFlowersBySeason(String season) {
        return flowerRepository.findAll()
                .stream()
                .filter(f -> f.getSeason() != null && f.getSeason().contains(season))
                .map(FlowerListResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 꽃 상세 정보 조회
     */
    public FlowerResponse getFlowerDetail(Long flowerId, Long userId) {
        Flower flower = flowerRepository.findById(flowerId)
                .orElseThrow(() -> new RuntimeException("꽃을 찾을 수 없습니다."));

        boolean isFavorite = false;
        if (userId != null) {
            isFavorite = favoriteRepository.existsByUserIdAndFlowerId(userId, flowerId);
        }

        return FlowerResponse.from(flower, isFavorite);
    }

    /**
     * 꽃 상세 정보 조회 (비로그인)
     */
    public FlowerResponse getFlowerDetail(Long flowerId) {
        return getFlowerDetail(flowerId, null);
    }

    /**
     * 키워드로 꽃 검색
     */
    public List<FlowerListResponse> getFlowersByKeyword(String keyword) {
        return flowerRepository.findAll()
                .stream()
                .filter(f -> f.getFlowerKeyword() != null &&
                        f.getFlowerKeyword().stream().anyMatch(k -> k.contains(keyword)))
                .map(FlowerListResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 꽃 이름으로 검색
     */
    public List<FlowerListResponse> searchFlowersByName(String name) {
        return flowerRepository.findByFlowerNameContaining(name)
                .stream()
                .map(FlowerListResponse::from)
                .collect(Collectors.toList());
    }
}
