package com.flower.dto.settings;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SettingsRequest {
    private Boolean pushNotifications;
    private Boolean emailNotifications;
    private String language;
    private String theme;
}
