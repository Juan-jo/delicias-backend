package com.delivery.app.mobile.app.dto;

public record MobileProductRecommendedDTO(
        Integer id,
        String name,
        String description,
        Double listPrice,
        String picture
) { }
