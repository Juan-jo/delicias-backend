package com.delivery.app.mobile.dtos;

import lombok.Builder;

@Builder
public record MobileDeliveryOrderDetailDTO(
    Integer orderId,
    Restaurant restaurant,
    UserInfo userInfo
) {


    @Builder
    public record Restaurant(
            Integer id,
            String name,
            String picture,
            String street
    ) { }


    @Builder
    public record UserInfo(
            Integer id,
            String name,
            String picture,
            String street
    ) { }

}
