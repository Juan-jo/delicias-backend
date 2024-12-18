package com.delivery.app.mobile.app.dto;


import lombok.Builder;

import java.util.List;

@Builder
public record MobileConfigDTO(
        List<Integer> availableRestaurants,
        Double costService,
        double latitude,
        double longitude
) { }

