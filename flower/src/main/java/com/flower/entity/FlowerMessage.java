package com.flower.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Flower_Message")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class FlowerMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Message_ID")
    private Long messageId;

    @Column(name = "User_ID", nullable = false)
    private Long userId;

    @Column(name = "Flower_ID", nullable = false)
    private Long flowerId;

    @Column(name = "Message", columnDefinition = "TEXT")
    private String message;

    @Column(name = "Created_At")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
