package com.delivery.app.mobile.user.dtos;

import lombok.Builder;

@Builder
public record MobileUserNearbyGeocodingDTO(
        String street,
        String address,
        double latitude,
        double longitude
) { }
