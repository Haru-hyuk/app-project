package com.flower.controller;

import com.flower.dto.shop.ShopResponse;
import com.flower.service.ShopService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shops")
@RequiredArgsConstructor
public class ShopController {

    private final ShopService shopService;

    @GetMapping("/nearby")
    public List<ShopResponse> nearby(
            @RequestParam(name = "lat") double lat,
            @RequestParam(name = "lng") double lng,
            @RequestParam(name = "radius", defaultValue = "1000") int radius,
            @RequestParam(name = "keyword", defaultValue = "꽃집") String keyword
    ) {
        return shopService.findNearbyShops(lat, lng, radius, keyword);
    }

}
