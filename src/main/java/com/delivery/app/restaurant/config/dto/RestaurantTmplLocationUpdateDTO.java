package com.delivery.app.restaurant.config.dto;


import com.delivery.app.configs.validation.common.OnUpdate;
import jakarta.validation.constraints.NotNull;

public record RestaurantTmplLocationUpdateDTO(

        @NotNull(message = "The parameter is mandatory", groups = { OnUpdate.class})
        Integer restaurantTmplId,

        @NotNull(message = "The parameter is mandatory", groups = { OnUpdate.class})
        Double latitude,

        @NotNull(message = "The parameter is mandatory", groups = { OnUpdate.class})
        Double longitude
) {
}
