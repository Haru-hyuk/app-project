package com.flower.repository;

import com.flower.entity.Favorites;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorites, Long> {

    // 사용자의 즐겨찾기 목록 조회
    List<Favorites> findByUserIdOrderByCreatedAtDesc(Long userId);

    // 특정 꽃이 즐겨찾기에 있는지 확인
    Optional<Favorites> findByUserIdAndFlowerId(Long userId, Long flowerId);

    // 즐겨찾기 존재 여부 확인
    boolean existsByUserIdAndFlowerId(Long userId, Long flowerId);

    // 특정 꽃 즐겨찾기 삭제
    void deleteByUserIdAndFlowerId(Long userId, Long flowerId);

    // 사용자의 즐겨찾기 개수
    long countByUserId(Long userId);
}
