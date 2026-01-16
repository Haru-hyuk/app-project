package com.flower.repository;

import com.flower.entity.Flower;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface FlowerRepository extends JpaRepository<Flower, Long> {

    // 오늘의 꽃 조회
    Optional<Flower> findByTodayFlower(LocalDate date);

    // 꽃 이름으로 검색
    List<Flower> findByFlowerNameContaining(String name);

    // 전체 꽃 목록 (최신순)
    List<Flower> findAllByOrderByFlowerIdDesc();
}
