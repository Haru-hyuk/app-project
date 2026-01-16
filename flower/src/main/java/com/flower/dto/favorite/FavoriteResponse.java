package com.flower.dto.favorite;

import com.flower.entity.Favorites;
import com.flower.entity.Flower;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FavoriteResponse {

    private Long favoriteId;
    private Long flowerId;
    private String flowerName;
    private List<String> floriography;
    private String imageUrl;
    private LocalDateTime createdAt;

    public static FavoriteResponse from(Favorites favorite, Flower flower) {
        return FavoriteResponse.builder()
                .favoriteId(favorite.getFavoriteId())
                .flowerId(flower.getFlowerId())
                .flowerName(flower.getFlowerName())
                .floriography(flower.getFloriography())
                .imageUrl(flower.getImageUrl())
                .createdAt(favorite.getCreatedAt())
                .build();
    }
}
