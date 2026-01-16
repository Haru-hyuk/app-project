package com.flower.controller;

import com.flower.dto.message.CardMessageRequest;
import com.flower.dto.message.CardMessageResponse;
import com.flower.service.CardMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
public class CardMessageController {

    private final CardMessageService cardMessageService;

    /**
     * 선물 카드 메시지 생성
     */
    @PostMapping("/message")
    public CardMessageResponse generateCardMessage(
            @RequestBody CardMessageRequest request
    ) {
        String message = cardMessageService.generateCardMessage(
                request.getFlowerName(),
                request.getFloriography(),
                request.getQuery()
        );

        return new CardMessageResponse(message);
    }
}
