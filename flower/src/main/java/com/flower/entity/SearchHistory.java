package com.flower.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "search_history",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "search_text"})
    }
)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer searchId;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "search_text", nullable = false, length = 200)
    private String searchText;

    @Column(name = "search_detail", nullable = true, length = 500)
    private String searchDetail;  // 검색 상세 정보 (nullable)

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    @PreUpdate
    void onSave() {
        this.createdAt = LocalDateTime.now();
    }
}
