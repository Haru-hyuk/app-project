package com.flower.service;

import com.flower.dto.user.PasswordChangeRequest;
import com.flower.dto.user.UserResponse;
import com.flower.dto.user.UserUpdateRequest;
import com.flower.entity.User;
import com.flower.repository.UserRepository;
import com.flower.security.SecurityUtil;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    /** 현재 로그인 유저 조회 */
    private User getLoginUser() {
        String email = SecurityUtil.getCurrentUserEmail();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
    }

    /** 현재 사용자 정보 조회 */
    public UserResponse getCurrentUser() {
        User user = getLoginUser();
        return UserResponse.from(user);
    }

    /** 사용자 정보 수정 */
    @Transactional
    public UserResponse updateUser(UserUpdateRequest request) {
        User user = getLoginUser();
        
        if (request.getNickname() != null && !request.getNickname().isBlank()) {
            user.changeNickname(request.getNickname());
        }
        if (request.getUserIntro() != null) {
            user.changeUserIntro(request.getUserIntro());
        }
        if (request.getProfileImage() != null) {
            user.changeProfileImage(request.getProfileImage());
        }
        
        return UserResponse.from(user);
    }

    /** 비밀번호 변경 */
    @Transactional
    public void changePassword(PasswordChangeRequest request) {

        User user = getLoginUser();

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("현재 비밀번호가 올바르지 않습니다.");
        }

        if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
            throw new RuntimeException("새 비밀번호가 서로 일치하지 않습니다.");
        }

        user.changePassword(passwordEncoder.encode(request.getNewPassword()));
    }
}
