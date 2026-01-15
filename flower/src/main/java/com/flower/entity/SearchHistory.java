package com.flower.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "SearchHistory")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class SearchHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Search_ID")
    private Long searchId;

    @Column(name = "User_ID", nullable = false)
    private Long userId;

    @Column(name = "Search_Detail", nullable = false, length = 500)
    private String searchDetail;

    @Column(name = "Created_At")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
