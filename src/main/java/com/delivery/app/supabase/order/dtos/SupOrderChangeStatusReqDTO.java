package com.delivery.app.supabase.order.dtos;

import lombok.Builder;

@Builder
public record SupOrderChangeStatusReqDTO(
        Integer orderId,
        String status
) {
}
