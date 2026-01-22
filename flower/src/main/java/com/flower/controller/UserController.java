package com.flower.controller;

import com.flower.dto.user.PasswordChangeRequest;
import com.flower.dto.user.UserResponse;
import com.flower.dto.user.UserUpdateRequest;
import com.flower.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /** 현재 사용자 정보 조회 */
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser() {
        return ResponseEntity.ok(userService.getCurrentUser());
    }

    /** 사용자 정보 수정 */
    @PutMapping("/me")
    public ResponseEntity<UserResponse> updateUser(@RequestBody UserUpdateRequest request) {
        return ResponseEntity.ok(userService.updateUser(request));
    }

    /** 비밀번호 변경 */
    @PutMapping("/me/password")
    public ResponseEntity<Map<String, String>> changePassword(@RequestBody PasswordChangeRequest request) {
        userService.changePassword(request);
        return ResponseEntity.ok(Map.of("message", "비밀번호가 변경되었습니다."));
    }

    /** 프로필 이미지 업로드 (URL만 받아서 저장) */
    @PostMapping("/me/profile-image")
    public ResponseEntity<Map<String, String>> uploadProfileImage(@RequestBody Map<String, String> body) {
        String imageUrl = body.get("imageUrl");
        if (imageUrl == null || imageUrl.isBlank()) {
            throw new RuntimeException("이미지 URL이 필요합니다.");
        }
        
        UserUpdateRequest request = new UserUpdateRequest(null, null, null, imageUrl);
        userService.updateUser(request);
        
        return ResponseEntity.ok(Map.of("message", "프로필 이미지가 업로드되었습니다.", "imageUrl", imageUrl));
    }
}
