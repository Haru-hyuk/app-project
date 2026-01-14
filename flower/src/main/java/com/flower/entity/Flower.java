package com.flower.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Flower")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Flower {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "꽃 고유번호")
    private Long flowerId;

    @Column(name = "꽃 이름", nullable = false)
    private String flowerName;

    @Column(name = "꽃말")
    private String flowerMeaning;

    @Column(name = "꽃 키워드")
    private String flowerKeywords;

    @Column(name = "꽃 이미지 주소", length = 500)
    private String flowerImageUrl;

    @Column(name = "주요 개화 계절", nullable = false, length = 50)
    private String bloomingSeason;

    @Column(name = "오늘의 꽃 날짜")
    private String todayFlowerDate;
}
