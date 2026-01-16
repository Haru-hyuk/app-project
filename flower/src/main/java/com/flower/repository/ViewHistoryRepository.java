package com.flower.repository;

import com.flower.entity.ViewHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ViewHistoryRepository extends JpaRepository<ViewHistory, Long> {

    // 사용자의 조회 기록 목록 (최신순)
    List<ViewHistory> findByUserIdOrderByCreatedAtDesc(Long userId);

    // 사용자의 최근 조회 기록 (제한)
    List<ViewHistory> findTop20ByUserIdOrderByCreatedAtDesc(Long userId);

    // 특정 조회 기록 삭제
    void deleteByViewIdAndUserId(Long viewId, Long userId);

    // 사용자의 모든 조회 기록 삭제
    void deleteByUserId(Long userId);
}
