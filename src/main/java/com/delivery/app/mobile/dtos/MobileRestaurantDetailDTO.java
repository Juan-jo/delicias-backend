package com.delivery.app.mobile.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;

import java.time.LocalTime;
import java.util.List;

@Builder
public record MobileRestaurantDetailDTO(
        Integer id,
        String name,
        String imageCover,
        RestaurantInfo info,
        List<RecommendedItem> recommended,
        List<Menu> menu
) {

    @Builder
    public record RestaurantInfo(
            String imageLogo,

            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm a")
            LocalTime hourStart,

            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm a")
            LocalTime hourEnd,

            boolean available,

            String address
    ) { }

    @Builder
    public record RecommendedItem(
            Integer id,
            String picture,
            String name,
            double priceList,
            String categoryName
    ) { }

    @Builder
    public record Menu(
            String name,
            List<ProductMenu> products
    ) {}

    @Builder
    public record ProductMenu(
            Integer id,
            String name,
            String picture,
            double priceList,
            String description
            ) {}
}
