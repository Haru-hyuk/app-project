package com.flower.dto.user;

import com.flower.entity.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private Integer userId;
    private String email;
    private String nickname;
    private String userName;
    private String userBirth;

    public static UserResponse from(User user) {
        return UserResponse.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .userName(user.getUserName())
                .userBirth(user.getUserBirth())
                .build();
    }
}
