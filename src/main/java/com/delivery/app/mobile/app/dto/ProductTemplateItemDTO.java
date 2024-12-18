package com.delivery.app.mobile.app.dto;

import lombok.Builder;

@Builder
public record ProductTemplateItemDTO(
        Integer id,
        String name,
        Double listPrice,
        String picture,
        String soldBy,
        Integer rate,
        String restaurantName
) { }
