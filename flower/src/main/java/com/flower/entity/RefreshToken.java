package com.flower.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Refresh_Token")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class RefreshToken {

    @Id
    @Column(name = "유저 이메일", nullable = false, unique = true)
    private String userEmail;

    @Column(name = "리프레쉬 토큰", length = 500)
    private String token;

    public void updateToken(String newToken) {
        this.token = newToken;
    }
}
