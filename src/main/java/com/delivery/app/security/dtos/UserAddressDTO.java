package com.delivery.app.security.dtos;

import com.delivery.app.security.enums.UserAddressType;
import lombok.Builder;

@Builder
public record UserAddressDTO(
        Integer id,
        UserAddressType addressType,
        String details,
        String companyName,
        String street,
        String address,
        double latitude,
        double longitude,
        String icon,
        String indications
) {
}
