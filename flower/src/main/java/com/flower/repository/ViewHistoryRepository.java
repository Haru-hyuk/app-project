package com.flower.repository;

import com.flower.entity.ViewHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ViewHistoryRepository extends JpaRepository<ViewHistory, Integer> {

    // 사용자의 조회 기록 목록 (최신순)
    List<ViewHistory> findByUserIdOrderByCreatedAtDesc(Integer userId);

    // 사용자의 최근 조회 기록 (제한)
    List<ViewHistory> findTop20ByUserIdOrderByCreatedAtDesc(Integer userId);

    // 특정 조회 기록 삭제
    void deleteByViewIdAndUserId(Integer viewId, Integer userId);

    // 사용자의 모든 조회 기록 삭제
    void deleteByUserId(Integer userId);
}
