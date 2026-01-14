package com.flower.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "유저 고유번호")
    private Long userId;

    @Column(name = "유저 이메일 (PK)", unique = true)
    private String email;

    @Column(name = "비밀번호")
    private String password;

    @Column(name = "닉네임", nullable = false)
    private String nickname;

    @Column(name = "이름", nullable = false)
    private String userName;

    @Column(name = "생년월일", nullable = false)
    private String userBirth;

    @Column(name = "가입 경로")
    private String signupPath;

    @Column(name = "생성일")
    private LocalDateTime createdAt;

    @Column(name = "수정일")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /* ---------- 도메인 메서드 ---------- */

    public void changePassword(String encodedPassword) {
        this.password = encodedPassword;
    }

    public void changeNickname(String nickname) {
        this.nickname = nickname;
    }
}
