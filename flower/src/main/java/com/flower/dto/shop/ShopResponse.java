package com.flower.dto.shop;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ShopResponse {
    private String name;
    private String address;
    private double lat;
    private double lng;
    private int distance;      // meter
    private String phone;
    private String placeUrl;
}
