package com.flower.repository;

import com.flower.entity.Flower;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface FlowerRepository extends JpaRepository<Flower, Integer> {

    // 오늘의 꽃 조회 (월일만 비교, 년도 무시)
    @Query(value = "SELECT * FROM Flower WHERE MONTH(Today_Flower) = :month AND DAY(Today_Flower) = :day LIMIT 1", nativeQuery = true)
    Optional<Flower> findByTodayFlowerMonthAndDay(@Param("month") int month, @Param("day") int day);

    // 오늘의 꽃 조회 (기존 메서드 - 호환성 유지)
    Optional<Flower> findByTodayFlower(LocalDate date);

    // 꽃 이름으로 검색
    List<Flower> findByFlowerNameContaining(String name);

    // 전체 꽃 목록 (최신순)
    List<Flower> findAllByOrderByFlowerIdDesc();

    // 계절별 꽃 목록 조회 (DB 레벨 JSON 필터링 - 최적화)
    @Query(value = "SELECT * FROM Flower WHERE JSON_CONTAINS(Season, JSON_QUOTE(:season))", nativeQuery = true)
    List<Flower> findBySeason(@Param("season") String season);

    // 계절별 랜덤 꽃 1개 조회 (DB 레벨에서 랜덤 선택 - 최적화)
    @Query(value = "SELECT * FROM Flower WHERE JSON_CONTAINS(Season, JSON_QUOTE(:season)) ORDER BY RAND() LIMIT 1", nativeQuery = true)
    Optional<Flower> findRandomFlowerBySeason(@Param("season") String season);

    // 계절별 랜덤 꽃 N개 조회
    @Query(value = "SELECT * FROM Flower WHERE JSON_CONTAINS(Season, JSON_QUOTE(:season)) ORDER BY RAND() LIMIT :limit", nativeQuery = true)
    List<Flower> findRandomFlowersBySeason(@Param("season") String season, @Param("limit") int limit);

    // 꽃말 또는 키워드로 검색 (DB 레벨 JSON 검색 - 최적화)
    @Query(value = "SELECT * FROM Flower WHERE " +
            "JSON_SEARCH(LOWER(Floriography), 'one', CONCAT('%', LOWER(:query), '%')) IS NOT NULL OR " +
            "JSON_SEARCH(LOWER(Flower_Keyword), 'one', CONCAT('%', LOWER(:query), '%')) IS NOT NULL",
            nativeQuery = true)
    List<Flower> findByFloriographyOrKeywordContaining(@Param("query") String query);

    // 키워드만으로 검색 (DB 레벨 JSON 검색 - 최적화)
    @Query(value = "SELECT * FROM Flower WHERE " +
            "JSON_SEARCH(LOWER(Flower_Keyword), 'one', CONCAT('%', LOWER(:keyword), '%')) IS NOT NULL",
            nativeQuery = true)
    List<Flower> findByKeywordContaining(@Param("keyword") String keyword);
}
