package com.flower.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Flower_Meanings")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class FlowerMeanings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "메시지 고유번호")
    private Long messageId;

    @Column(name = "유저 고유번호", nullable = false)
    private String userId;

    @Column(name = "꽃 고유번호", nullable = false)
    private Long flowerId;

    @Column(name = "꽃 메시지", columnDefinition = "TEXT")
    private String flowerMessage;

    @Column(name = "생성일")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
