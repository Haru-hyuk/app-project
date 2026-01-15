package com.flower.repository;

import com.flower.entity.SearchHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SearchHistoryRepository
        extends JpaRepository<SearchHistory, Long> {

    Optional<SearchHistory> findByUserIdAndSearchText(
            Long userId, String searchText
    );

    List<SearchHistory> findTop10ByUserIdOrderByCreatedAtDesc(Long userId);

    void deleteByUserIdAndSearchText(Long userId, String searchText);

    void deleteByUserId(Long userId);
}
