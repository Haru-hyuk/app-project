package com.flower.search.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class DeepSeekChatResponse {

    private List<Choice> choices;

    @Getter
    public static class Choice {
        private Message message;
    }

    @Getter
    public static class Message {
        private String content;
    }
}
