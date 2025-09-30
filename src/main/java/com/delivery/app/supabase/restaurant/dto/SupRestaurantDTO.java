package com.delivery.app.supabase.restaurant.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record SupRestaurantDTO(
        Integer id,
        String  name,

        @JsonProperty("logo_url")
        String logoUrl,
        String address,
        Integer rating
) {

}
