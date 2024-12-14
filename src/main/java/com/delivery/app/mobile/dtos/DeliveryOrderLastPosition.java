package com.delivery.app.mobile.dtos;

import com.delivery.app.configs.validation.common.OnUpdate;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record DeliveryOrderLastPosition(
        @NotNull(message = "The parameter is mandatory.", groups = { OnUpdate.class })
        double latitude,

        @NotNull(message = "The parameter is mandatory.", groups = { OnUpdate.class })
        double longitude,

        @NotNull(message = "The parameter is mandatory.", groups = { OnUpdate.class })
        Integer orderId
) { }
