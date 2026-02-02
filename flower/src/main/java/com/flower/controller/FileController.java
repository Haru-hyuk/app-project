package com.flower.controller;

import com.flower.entity.User;
import com.flower.repository.UserRepository;
import com.flower.security.SecurityUtil;
import com.flower.service.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final S3Service s3Service;
    private final UserRepository userRepository;

    /**
     * 프로필 이미지 업로드
     * POST /api/files/profile-image
     */
    @PostMapping("/profile-image")
    public ResponseEntity<?> uploadProfileImage(
            @RequestParam("file") MultipartFile file) {

        String email = SecurityUtil.getCurrentUserEmail();
        log.info("=== 프로필 이미지 업로드 요청 ===");
        log.info("파일명: {}, 크기: {} bytes, Content-Type: {}",
                file.getOriginalFilename(), file.getSize(), file.getContentType());
        log.info("사용자: {}", email);

        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "파일이 비어있습니다."
                ));
            }

            // 파일 타입 검증
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "이미지 파일만 업로드 가능합니다."
                ));
            }

            // 파일 크기 검증 (5MB 제한)
            if (file.getSize() > 5 * 1024 * 1024) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "파일 크기는 5MB 이하여야 합니다."
                ));
            }

            // 사용자 조회
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

            // 기존 프로필 이미지가 S3에 있으면 삭제
            if (user.getProfileImage() != null && user.getProfileImage().contains("s3")) {
                s3Service.deleteFile(user.getProfileImage());
            }

            // S3에 업로드
            log.info("S3 업로드 시작...");
            String imageUrl = s3Service.uploadProfileImage(file, user.getUserId());
            log.info("S3 업로드 완료: {}", imageUrl);

            // DB에 URL 저장
            user.changeProfileImage(imageUrl);
            userRepository.save(user);
            log.info("DB 저장 완료");

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "imageUrl", imageUrl,
                    "message", "프로필 이미지가 업로드되었습니다."
            ));

        } catch (Exception e) {
            log.error("프로필 이미지 업로드 실패: {}", e.getMessage());
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "message", "이미지 업로드에 실패했습니다: " + e.getMessage()
            ));
        }
    }

    /**
     * 프로필 이미지 삭제
     * DELETE /api/files/profile-image
     */
    @DeleteMapping("/profile-image")
    public ResponseEntity<?> deleteProfileImage() {

        try {
            String email = SecurityUtil.getCurrentUserEmail();
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

            // S3에서 삭제
            if (user.getProfileImage() != null && user.getProfileImage().contains("s3")) {
                s3Service.deleteFile(user.getProfileImage());
            }

            // DB에서 URL 제거
            user.changeProfileImage(null);
            userRepository.save(user);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "프로필 이미지가 삭제되었습니다."
            ));

        } catch (Exception e) {
            log.error("프로필 이미지 삭제 실패: {}", e.getMessage());
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "message", "이미지 삭제에 실패했습니다: " + e.getMessage()
            ));
        }
    }
}
