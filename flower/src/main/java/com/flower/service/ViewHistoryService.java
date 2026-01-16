package com.flower.service;

import com.flower.dto.history.ViewHistoryResponse;
import com.flower.entity.Flower;
import com.flower.entity.ViewHistory;
import com.flower.repository.FlowerRepository;
import com.flower.repository.ViewHistoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ViewHistoryService {

    private final ViewHistoryRepository viewHistoryRepository;
    private final FlowerRepository flowerRepository;

    /**
     * 조회 기록 목록 조회
     */
    public List<ViewHistoryResponse> getViewHistory(Long userId) {
        List<ViewHistory> histories = viewHistoryRepository.findTop20ByUserIdOrderByCreatedAtDesc(userId);

        // 꽃 정보를 한 번에 조회
        List<Long> flowerIds = histories.stream()
                .map(ViewHistory::getFlowerId)
                .collect(Collectors.toList());

        Map<Long, Flower> flowerMap = flowerRepository.findAllById(flowerIds)
                .stream()
                .collect(Collectors.toMap(Flower::getFlowerId, f -> f));

        return histories.stream()
                .map(h -> ViewHistoryResponse.from(h, flowerMap.get(h.getFlowerId())))
                .collect(Collectors.toList());
    }

    /**
     * 조회 기록 저장
     */
    @Transactional
    public void saveViewHistory(Long userId, Long flowerId) {
        // 꽃 존재 확인
        if (!flowerRepository.existsById(flowerId)) {
            throw new RuntimeException("꽃을 찾을 수 없습니다.");
        }

        ViewHistory history = ViewHistory.builder()
                .userId(userId)
                .flowerId(flowerId)
                .build();

        viewHistoryRepository.save(history);
    }

    /**
     * 특정 조회 기록 삭제
     */
    @Transactional
    public void deleteViewHistory(Long userId, Long viewId) {
        viewHistoryRepository.deleteByViewIdAndUserId(viewId, userId);
    }

    /**
     * 전체 조회 기록 삭제
     */
    @Transactional
    public void deleteAllViewHistory(Long userId) {
        viewHistoryRepository.deleteByUserId(userId);
    }
}
