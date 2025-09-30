package com.delivery.app.pos.order.dto;

import com.delicias.soft.services.core.common.OrderStatus;
import lombok.Builder;

@Builder
public record FilterOrdersRestaurantResponseDTO(
        Integer id,
        OrderStatus status
) {
}
