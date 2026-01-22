package com.flower.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.List;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "Flower")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Flower {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Flower_ID")
    private Integer flowerId;

    @Column(name = "Flower_Name", nullable = false)
    private String flowerName;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "Floriography", columnDefinition = "JSON")
    private List<String> floriography;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "Flower_Keyword", columnDefinition = "JSON")
    private List<String> flowerKeyword;

    @Column(name = "Flower_Origin", columnDefinition = "TEXT")
    private String flowerOrigin;

    @Column(name = "Flower_Describe", columnDefinition = "TEXT")
    private String flowerDescribe;

    @Column(name = "Image_Url", length = 500)
    private String imageUrl;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "Season", columnDefinition = "JSON")
    private List<String> season;

    @Column(name = "Today_Flower")
    private LocalDate todayFlower;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "Embedding", columnDefinition = "JSON", nullable = false)
    private List<Double> embedding;
}
