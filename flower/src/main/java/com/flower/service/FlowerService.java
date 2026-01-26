package com.flower.service;

import com.flower.dto.flower.FlowerListResponse;
import com.flower.dto.flower.FlowerResponse;
import com.flower.entity.Flower;
import com.flower.repository.FavoriteRepository;
import com.flower.repository.FlowerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.PageRequest;

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
     * 오늘의 꽃 조회 (월일만 비교)
     */
    public FlowerResponse getTodayFlower() {
        LocalDate today = LocalDate.now();
        int month = today.getMonthValue();
        int day = today.getDayOfMonth();
        return flowerRepository.findByTodayFlowerMonthAndDay(month, day)
                .map(FlowerResponse::from)
                .orElse(null);
    }

    /**
     * 계절별 꽃 목록 조회 (최적화: DB 레벨 JSON 필터링)
     */
    public List<FlowerListResponse> getFlowersBySeason(String season) {
        return flowerRepository.findBySeason(season)
                .stream()
                .map(FlowerListResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 계절별 랜덤 꽃 1개 조회 (최적화: DB 레벨에서 랜덤 선택)
     */
    public FlowerListResponse getRandomFlowerBySeason(String season) {
        return flowerRepository
                .findRandomFlowerBySeason(season, PageRequest.of(0, 1))
                .stream()
                .findFirst()
                .map(FlowerListResponse::from)
                .orElse(null);
    }

    /**
     * 모든 계절의 랜덤 꽃 한 번에 조회 (4번 호출 → 1번 호출로 최적화)
     */
    public java.util.Map<String, FlowerListResponse> getRandomFlowersForAllSeasons() {
        java.util.Map<String, FlowerListResponse> result = new java.util.LinkedHashMap<>();
        String[] seasons = {"spring", "summer", "autumn", "winter"};

        for (String season : seasons) {
            FlowerListResponse flower = getRandomFlowerBySeason(season);
            if (flower != null) {
                result.put(season, flower);
            }
        }
        return result;
    }

    /**
     * 꽃 상세 정보 조회
     */
    public FlowerResponse getFlowerDetail(Integer flowerId, Integer userId) {
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
    public FlowerResponse getFlowerDetail(Integer flowerId) {
        return getFlowerDetail(flowerId, null);
    }

    /**
     * 키워드로 꽃 검색 - DB 레벨 최적화
     */
    public List<FlowerListResponse> getFlowersByKeyword(String keyword) {
        return flowerRepository.findByKeywordContaining(keyword)
                .stream()
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

    /**
     * 꽃말로 검색 (floriography와 keyword 모두 검색) - DB 레벨 최적화
     */
    public List<FlowerListResponse> searchFlowersByFloriography(String query) {
        return flowerRepository.findByFloriographyOrKeywordContaining(query)
                .stream()
                .map(FlowerListResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 검색어가 flower 테이블의 꽃말/키워드에 존재하는지 확인 - DB 레벨 최적화
     */
    public boolean existsInFloriographyOrKeyword(String query) {
        return !flowerRepository.findByFloriographyOrKeywordContaining(query).isEmpty();
    }
}
