package com.delivery.app.supabase.order.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.UUID;

@Builder
public record SupOrderLineDTO(
        Integer id,
        @JsonProperty("product_name")
        String productName,
        Integer quantity,
        Double price,
        String description,
        @JsonProperty("order_id")
        Integer orderId
) {
}
