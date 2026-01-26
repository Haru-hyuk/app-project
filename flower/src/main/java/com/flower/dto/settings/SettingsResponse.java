package com.flower.dto.settings;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SettingsResponse {
    private Boolean pushNotifications;
    private Boolean emailNotifications;
    private String language;
    private String theme;
    private String appVersion;

    public static SettingsResponse defaultSettings() {
        return SettingsResponse.builder()
                .pushNotifications(true)
                .emailNotifications(true)
                .language("ko")
                .theme("light")
                .appVersion("1.0.0")
                .build();
    }
}
