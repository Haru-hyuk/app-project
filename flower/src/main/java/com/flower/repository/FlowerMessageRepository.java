package com.flower.repository;

import com.flower.entity.FlowerMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FlowerMessageRepository extends JpaRepository<FlowerMessage, Integer> {

    // 사용자 ID와 꽃 ID로 메시지 조회
    Optional<FlowerMessage> findByUserIdAndFlowerId(Integer userId, Integer flowerId);

    // 사용자의 모든 메시지 조회
    List<FlowerMessage> findByUserIdOrderByCreatedAtDesc(Integer userId);

    // 꽃 ID로 메시지 조회
    List<FlowerMessage> findByFlowerIdOrderByCreatedAtDesc(Integer flowerId);
}
