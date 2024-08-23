package com.delivery.app.mobile.dtos;


import lombok.Builder;

import java.util.List;

@Builder
public record MobileConfigDTO(
        List<Integer> availableRestaurants,
        Double costService,
        double latitude,
        double longitude
) { }

