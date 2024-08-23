package com.delivery.app.mobile.dtos;

import lombok.Builder;

@Builder
public record MobileGeocodingDTO(
        String street,
        String address,
        double latitude,
        double longitude
) { }
