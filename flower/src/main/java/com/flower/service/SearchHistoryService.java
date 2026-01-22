package com.flower.service;

import com.flower.entity.SearchHistory;
import com.flower.repository.SearchHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchHistoryService {

    private final SearchHistoryRepository repository;
    private static final int MAX_HISTORY = 10;

    @Transactional
    public void save(Integer userId, String query) {

        repository.findByUserIdAndSearchText(userId, query)
                .ifPresentOrElse(
                        existing -> {
                            // 아무것도 하지 않음
                            // → dirty checking으로 @PreUpdate 자동 실행
                        },
                        () -> repository.save(
                                SearchHistory.builder()
                                        .userId(userId)
                                        .searchText(query)
                                        .build()
                        )
                );

        List<SearchHistory> histories =
                repository.findTop10ByUserIdOrderByCreatedAtDesc(userId);

        if (histories.size() > MAX_HISTORY) {
            histories.subList(MAX_HISTORY, histories.size())
                    .forEach(repository::delete);
        }
    }

    public List<String> getRecent(Integer userId) {
        return repository
                .findTop10ByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(SearchHistory::getSearchText)
                .toList();
    }

    @Transactional
    public void deleteOne(Integer userId, String query) {
        repository.deleteByUserIdAndSearchText(userId, query);
    }

    @Transactional
    public void deleteAll(Integer userId) {
        repository.deleteByUserId(userId);
    }
}
