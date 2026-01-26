package com.flower.repository;

import com.flower.entity.PopularKeyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PopularKeywordRepository extends JpaRepository<PopularKeyword, Integer> {

    Optional<PopularKeyword> findByKeyword(String keyword);

    /** 인기 키워드 조회 (검색 횟수 기준 내림차순) */
    @Query("SELECT pk FROM PopularKeyword pk ORDER BY pk.count DESC")
    List<PopularKeyword> findPopularKeywords();

    /** 트렌딩 키워드 조회 (최근 7일간 업데이트된 키워드 중 검색 횟수 기준) */
    @Query(value = "SELECT * FROM popular_keyword WHERE Updated_At >= DATE_SUB(NOW(), INTERVAL 7 DAY) ORDER BY Count DESC", nativeQuery = true)
    List<PopularKeyword> findTrendingKeywords();
}
