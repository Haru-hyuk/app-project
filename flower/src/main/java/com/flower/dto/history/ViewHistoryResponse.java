package com.flower.dto.history;

import com.flower.entity.Flower;
import com.flower.entity.ViewHistory;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ViewHistoryResponse {

    private Integer viewId;
    private Integer flowerId;
    private String flowerName;
    private String imageUrl;
    private LocalDateTime createdAt;

    public static ViewHistoryResponse from(ViewHistory history, Flower flower) {
        return ViewHistoryResponse.builder()
                .viewId(history.getViewId())
                .flowerId(flower.getFlowerId())
                .flowerName(flower.getFlowerName())
                .imageUrl(flower.getImageUrl())
                .createdAt(history.getCreatedAt())
                .build();
    }
}
