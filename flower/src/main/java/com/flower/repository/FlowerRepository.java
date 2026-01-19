package com.flower.repository;

import com.flower.entity.Flower;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface FlowerRepository extends JpaRepository<Flower, Long> {

    // 오늘의 꽃 조회 (월일만 비교, 년도 무시)
    @Query(value = "SELECT * FROM Flower WHERE MONTH(Today_Flower) = :month AND DAY(Today_Flower) = :day LIMIT 1", nativeQuery = true)
    Optional<Flower> findByTodayFlowerMonthAndDay(@Param("month") int month, @Param("day") int day);

    // 오늘의 꽃 조회 (기존 메서드 - 호환성 유지)
    Optional<Flower> findByTodayFlower(LocalDate date);

    // 꽃 이름으로 검색
    List<Flower> findByFlowerNameContaining(String name);

    // 전체 꽃 목록 (최신순)
    List<Flower> findAllByOrderByFlowerIdDesc();
}
