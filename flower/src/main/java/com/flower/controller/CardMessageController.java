package com.flower.controller;

import com.flower.dto.message.CardMessageRequest;
import com.flower.dto.message.CardMessageResponse;
import com.flower.entity.User;
import com.flower.repository.UserRepository;
import com.flower.security.SecurityUtil;
import com.flower.service.CardMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
public class CardMessageController {

    private final CardMessageService cardMessageService;
    private final UserRepository userRepository;

    /**
     * 선물 카드 메시지 생성 + DB 저장
     */
    @PostMapping("/message")
    public CardMessageResponse generateCardMessage(
            @RequestBody CardMessageRequest request
    ) {
        // 현재 로그인한 사용자 ID 조회
        String email = SecurityUtil.getCurrentUserEmail();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        Integer userId = user.getUserId();

        String message = cardMessageService.generateAndSaveCardMessage(
                userId,
                request.getFlowerId(),
                request.getFlowerName(),
                request.getFloriography(),
                request.getQuery()
        );

        return new CardMessageResponse(message);
    }
}
