package com.delivery.app.supabase.order.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.UUID;

@Builder
public record SupOrderDTO(
        Integer id,
        @JsonProperty("user_id")
        UUID userId,
        String status,
        @JsonProperty("restaurant_id")
        Integer restaurantId,
        @JsonProperty("delivery_id")
        UUID deliveryId
) { }
