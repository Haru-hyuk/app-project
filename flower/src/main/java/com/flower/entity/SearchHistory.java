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
    private Long searchId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "search_text", nullable = false, length = 200)
    private String searchText;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    @PreUpdate
    void onSave() {
        this.createdAt = LocalDateTime.now();
    }
}
