package com.flower.repository;

import com.flower.entity.SearchHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SearchHistoryRepository
        extends JpaRepository<SearchHistory, Integer> {

    Optional<SearchHistory> findByUserIdAndSearchText(
            Integer userId, String searchText
    );

    List<SearchHistory> findTop10ByUserIdOrderByCreatedAtDesc(Integer userId);

    void deleteByUserIdAndSearchText(Integer userId, String searchText);

    void deleteByUserId(Integer userId);
}
