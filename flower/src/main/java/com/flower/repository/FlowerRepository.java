package com.flower.repository;

import com.flower.entity.Flower;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface FlowerRepository extends JpaRepository<Flower, Integer> {

    /* =========================
       오늘의 꽃
       ========================= */

    // 오늘의 꽃 조회 (월/일만 비교, 연도 무시)
    @Query(
        value = "SELECT * FROM Flower " +
                "WHERE MONTH(Today_Flower) = :month " +
                "AND DAY(Today_Flower) = :day " +
                "LIMIT 1",
        nativeQuery = true
    )
    Optional<Flower> findByTodayFlowerMonthAndDay(
            @Param("month") int month,
            @Param("day") int day
    );

    // 기존 방식 (LocalDate 전체 비교)
    Optional<Flower> findByTodayFlower(LocalDate date);

    /* =========================
       기본 조회
       ========================= */

    // 꽃 이름 검색
    List<Flower> findByFlowerNameContaining(String name);

    // 전체 꽃 목록 (최신순)
    List<Flower> findAllByOrderByFlowerIdDesc();

    /* =========================
       계절 기반 조회 (JSON)
       ========================= */

    // 계절별 꽃 목록
    @Query(
        value = "SELECT * FROM Flower " +
                "WHERE JSON_CONTAINS(Season, JSON_QUOTE(:season))",
        nativeQuery = true
    )
    List<Flower> findBySeason(@Param("season") String season);

    // 계절별 랜덤 꽃 1개
    @Query(
        value = "SELECT * FROM Flower " +
                "WHERE JSON_CONTAINS(Season, JSON_QUOTE(:season)) " +
                "ORDER BY RAND()",
        nativeQuery = true
    )
    List<Flower> findRandomFlowerBySeason(
            @Param("season") String season,
            Pageable pageable
    );

    // 계절별 랜덤 꽃 N개
    @Query(
        value = "SELECT * FROM Flower " +
                "WHERE JSON_CONTAINS(Season, JSON_QUOTE(:season)) " +
                "ORDER BY RAND()",
        nativeQuery = true
    )
    List<Flower> findRandomFlowersBySeason(
            @Param("season") String season,
            Pageable pageable
    );

    /* =========================
       꽃말 / 키워드 검색 (JSON)
       ========================= */

    // 꽃말 또는 키워드 검색
    @Query(
        value = "SELECT * FROM Flower WHERE " +
                "JSON_SEARCH(LOWER(Floriography), 'one', CONCAT('%', LOWER(:query), '%')) IS NOT NULL " +
                "OR JSON_SEARCH(LOWER(Flower_Keyword), 'one', CONCAT('%', LOWER(:query), '%')) IS NOT NULL",
        nativeQuery = true
    )
    List<Flower> findByFloriographyOrKeywordContaining(
            @Param("query") String query
    );

    // 키워드만 검색
    @Query(
        value = "SELECT * FROM Flower WHERE " +
                "JSON_SEARCH(LOWER(Flower_Keyword), 'one', CONCAT('%', LOWER(:keyword), '%')) IS NOT NULL",
        nativeQuery = true
    )
    List<Flower> findByKeywordContaining(
            @Param("keyword") String keyword
    );
}
