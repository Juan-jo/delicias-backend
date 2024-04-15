package com.delivery.app.pos.order.dto;

import com.delivery.app.pos.enums.OrderStatus;
import lombok.Builder;

@Builder
public record FilterOrdersRestaurantResponseDTO(
        Integer id,
        OrderStatus status
) {
}
