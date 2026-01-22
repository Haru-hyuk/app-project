package com.flower.service;

import com.flower.dto.favorite.FavoriteResponse;
import com.flower.entity.Favorites;
import com.flower.entity.Flower;
import com.flower.repository.FavoriteRepository;
import com.flower.repository.FlowerRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final FlowerRepository flowerRepository;

    /**
     * 즐겨찾기 목록 조회
     */
    public List<FavoriteResponse> getFavorites(Integer userId) {
        List<Favorites> favorites = favoriteRepository.findByUserIdOrderByCreatedAtDesc(userId);

        // 꽃 정보를 한 번에 조회
        List<Integer> flowerIds = favorites.stream()
                .map(Favorites::getFlowerId)
                .collect(Collectors.toList());

        Map<Integer, Flower> flowerMap = flowerRepository.findAllById(flowerIds)
                .stream()
                .collect(Collectors.toMap(Flower::getFlowerId, f -> f));

        return favorites.stream()
                .map(fav -> FavoriteResponse.from(fav, flowerMap.get(fav.getFlowerId())))
                .collect(Collectors.toList());
    }

    /**
     * 즐겨찾기 추가
     */
    @Transactional
    public FavoriteResponse addFavorite(Integer userId, Integer flowerId) {
        // 이미 즐겨찾기에 있는지 확인
        if (favoriteRepository.existsByUserIdAndFlowerId(userId, flowerId)) {
            throw new RuntimeException("이미 즐겨찾기에 추가된 꽃입니다.");
        }

        // 꽃 존재 확인
        Flower flower = flowerRepository.findById(flowerId)
                .orElseThrow(() -> new RuntimeException("꽃을 찾을 수 없습니다."));

        Favorites favorite = Favorites.builder()
                .userId(userId)
                .flowerId(flowerId)
                .build();

        Favorites saved = favoriteRepository.save(favorite);
        return FavoriteResponse.from(saved, flower);
    }

    /**
     * 즐겨찾기 삭제
     */
    @Transactional
    public void removeFavorite(Integer userId, Integer flowerId) {
        favoriteRepository.deleteByUserIdAndFlowerId(userId, flowerId);
    }

    /**
     * 즐겨찾기 여부 확인
     */
    public Map<String, Object> checkFavorite(Integer userId, Integer flowerId) {
        boolean isFavorite = favoriteRepository.existsByUserIdAndFlowerId(userId, flowerId);
        Integer favoriteId = null;

        if (isFavorite) {
            favoriteId = favoriteRepository.findByUserIdAndFlowerId(userId, flowerId)
                    .map(Favorites::getFavoriteId)
                    .orElse(null);
        }

        return Map.of(
                "isFavorite", isFavorite,
                "favoriteId", favoriteId != null ? favoriteId : 0
        );
    }

    /**
     * 즐겨찾기 개수 조회
     */
    public long getFavoriteCount(Integer userId) {
        return favoriteRepository.countByUserId(userId);
    }
}
