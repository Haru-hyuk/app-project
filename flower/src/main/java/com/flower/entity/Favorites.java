package com.flower.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Favorite")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Favorites {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "즐겨찾기 고유번호")
    private Long favoriteId;

    @Column(name = "유저 고유번호", nullable = false)
    private Long userId;

    @Column(name = "꽃 고유번호", nullable = false)
    private Long flowerId;

    @Column(name = "생성일")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
