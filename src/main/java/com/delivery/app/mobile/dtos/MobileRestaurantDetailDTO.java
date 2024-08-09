package com.delivery.app.mobile.dtos;

import lombok.Builder;
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
            String hourStart,
            String hourEnd,
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
            Integer id,
            String name,
            List<ProductMenu> products
    ) {}

    @Builder
    public record ProductMenu(
            Integer id,
            String name,
            String picture,
            double priceList
            ) {}
}
