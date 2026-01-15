package com.flower.search.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
@AllArgsConstructor
public class DeepSeekChatRequest {

    private String model;
    private List<Map<String, String>> messages;
    private double temperature;
}
