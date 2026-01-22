package com.flower.controller;

import com.flower.dto.settings.SettingsRequest;
import com.flower.dto.settings.SettingsResponse;
import com.flower.service.SettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/settings")
@RequiredArgsConstructor
public class SettingsController {

    private final SettingsService settingsService;

    /** 사용자 설정 조회 */
    @GetMapping
    public ResponseEntity<SettingsResponse> getSettings() {
        return ResponseEntity.ok(settingsService.getSettings());
    }

    /** 사용자 설정 저장 */
    @PutMapping
    public ResponseEntity<SettingsResponse> saveSettings(@RequestBody SettingsRequest request) {
        return ResponseEntity.ok(settingsService.saveSettings(request));
    }
}
