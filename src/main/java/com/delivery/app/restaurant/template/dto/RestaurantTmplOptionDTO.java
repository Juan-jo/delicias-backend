package com.delivery.app.restaurant.template.dto;

import lombok.Builder;

@Builder
public record RestaurantTmplOptionDTO(
        Integer id,
        String name,
        String picture
) { }
