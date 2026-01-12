package com.flower.dto.user;

import lombok.Getter;

@Getter
public class UserUpdateRequest {
    private String nickname;
    private String preference;
    private String userBirth;
}
