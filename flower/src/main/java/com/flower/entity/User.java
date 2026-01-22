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
    @Column(name = "User_ID")
    private Integer userId;

    @Column(name = "User_Email", unique = true)
    private String email;

    @Column(name = "User_PW")
    private String password;

    @Column(name = "Nickname", nullable = false)
    private String nickname;

    @Column(name = "User_name", nullable = false)
    private String userName;

    @Column(name = "User_birth", nullable = false)
    private String userBirth;

    @Column(name = "Oath_Provider")
    private String oauthProvider;

    @Column(name = "User_intro", length = 200)
    private String userIntro;

    @Column(name = "Profile_image", length = 500)
    private String profileImage;

    @Column(name = "Created_At")
    private LocalDateTime createdAt;

    @Column(name = "Updated_At")
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

    public void changeUserIntro(String userIntro) {
        this.userIntro = userIntro;
    }

    public void changeProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public void updateProfile(String nickname, String userIntro, String profileImage) {
        if (nickname != null && !nickname.isBlank()) {
            this.nickname = nickname;
        }
        if (userIntro != null) {
            this.userIntro = userIntro;
        }
        if (profileImage != null) {
            this.profileImage = profileImage;
        }
    }
}
