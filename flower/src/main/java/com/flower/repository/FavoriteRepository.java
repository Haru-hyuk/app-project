package com.flower.repository;

import com.flower.entity.Favorites;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorites, Integer> {

    // 사용자의 즐겨찾기 목록 조회
    List<Favorites> findByUserIdOrderByCreatedAtDesc(Integer userId);

    // 특정 꽃이 즐겨찾기에 있는지 확인
    Optional<Favorites> findByUserIdAndFlowerId(Integer userId, Integer flowerId);

    // 즐겨찾기 존재 여부 확인
    boolean existsByUserIdAndFlowerId(Integer userId, Integer flowerId);

    // 특정 꽃 즐겨찾기 삭제
    void deleteByUserIdAndFlowerId(Integer userId, Integer flowerId);

    // 사용자의 즐겨찾기 개수
    long countByUserId(Integer userId);
}
