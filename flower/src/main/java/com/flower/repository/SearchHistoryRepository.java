package com.flower.repository;

import com.flower.entity.SearchHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.util.List;
import java.util.Optional;

public interface SearchHistoryRepository
        extends JpaRepository<SearchHistory, Integer> {

    Optional<SearchHistory> findByUserIdAndSearchText(
            Integer userId, String searchText
    );

    List<SearchHistory> findTop10ByUserIdOrderByCreatedAtDesc(Integer userId);

    @Modifying
    void deleteByUserIdAndSearchText(Integer userId, String searchText);

    @Modifying
    void deleteByUserId(Integer userId);
}
