package com.delivery.app.mobile.app.dto;

import lombok.Builder;

@Builder
public record MobileRestaurantItemDTO(
        Integer id,
        String name,
        String description,
        String picture,
        String cover
) { }
