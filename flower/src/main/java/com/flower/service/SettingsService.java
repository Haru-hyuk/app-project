package com.flower.service;

import com.flower.dto.settings.SettingsRequest;
import com.flower.dto.settings.SettingsResponse;
import com.flower.entity.User;
import com.flower.repository.UserRepository;
import com.flower.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SettingsService {

    private final UserRepository userRepository;

    /** 사용자 설정 조회 */
    public SettingsResponse getSettings() {
        // 현재는 기본값 반환 (나중에 User 엔티티에 설정 필드 추가 가능)
        return SettingsResponse.defaultSettings();
    }

    /** 사용자 설정 저장 */
    @Transactional
    public SettingsResponse saveSettings(SettingsRequest request) {
        String email = SecurityUtil.getCurrentUserEmail();
        // 사용자 존재 확인 (나중에 User 엔티티에 설정 필드 추가하여 저장 가능)
        userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 현재는 기본값 반환 (나중에 User 엔티티에 설정 필드 추가하여 저장 가능)
        // TODO: User 엔티티에 설정 필드 추가 후 저장 로직 구현
        
        return SettingsResponse.builder()
                .pushNotifications(request.getPushNotifications() != null ? request.getPushNotifications() : true)
                .emailNotifications(request.getEmailNotifications() != null ? request.getEmailNotifications() : true)
                .language(request.getLanguage() != null ? request.getLanguage() : "ko")
                .theme(request.getTheme() != null ? request.getTheme() : "light")
                .appVersion("1.0.0")
                .build();
    }
}
