package com.delivery.app.delicias.general.dto;


import com.delivery.app.configs.validation.common.OnUpdate;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record MobileConfigDTO(

        @NotNull(message = "The parameter is mandatory.", groups = { OnUpdate.class })
        List<Integer> availableRestaurants,

        @NotNull(message = "The parameter is mandatory.", groups = { OnUpdate.class })
        Double minimumShippingCost,

        @NotNull(message = "The parameter is mandatory.", groups = { OnUpdate.class })
        Double minimumShippingDistance,

        @NotNull(message = "The parameter is mandatory.", groups = { OnUpdate.class })
        Double shippingCostPerKm,

        boolean hasMessageShippingCost,

        String messageShippingCost
) { }

