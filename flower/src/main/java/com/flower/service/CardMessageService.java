package com.flower.service;

import com.flower.entity.FlowerMessage;
import com.flower.external.deepseek.DeepSeekChatClient;
import com.flower.repository.FlowerMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CardMessageService {

    private final DeepSeekChatClient deepSeekChatClient;
    private final FlowerMessageRepository flowerMessageRepository;

    public String generateCardMessage(
            String flowerName,
            List<String> floriography,
            String query
    ) {
        String prompt = buildPrompt(flowerName, floriography, query);
        return deepSeekChatClient.generateMessage(prompt);
    }

    /**
     * 메시지 생성 + DB 저장
     */
    public String generateAndSaveCardMessage(
            Integer userId,
            Integer flowerId,
            String flowerName,
            List<String> floriography,
            String query
    ) {
        String message = generateCardMessage(flowerName, floriography, query);

        // DB에 저장
        FlowerMessage flowerMessage = FlowerMessage.builder()
                .userId(userId)
                .flowerId(flowerId)
                .message(message)
                .build();
        flowerMessageRepository.save(flowerMessage);

        return message;
    }

    private String buildPrompt(
            String flowerName,
            List<String> floriography,
            String query
    ) {
        return """
                너는 꽃 선물 카드 문구를 작성하는 작가야.

                [꽃 정보]
                - 꽃 이름: %s
                - 꽃말: %s

                [상황]
                - %s

                [작성 조건]
                - 카드 안에 들어갈 메시지
                - 1~2문장
                - 따뜻하고 진심 어린 톤
                - 과하지 않게
                - 한국어
                - 따옴표, 이모지, 인사말 금지

                메시지만 출력해.
                """.formatted(
                flowerName,
                String.join(", ", floriography),
                query
        );
    }
}
