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
    @Column(name = "User_Email", nullable = false, unique = true)
    private String userEmail;

    @Column(name = "Refresh_Token", length = 500, nullable = false)
    private String token;

    public void updateToken(String newToken) {
        this.token = newToken;
    }
}
