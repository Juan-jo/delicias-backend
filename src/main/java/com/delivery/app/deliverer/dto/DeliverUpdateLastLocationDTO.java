package com.delivery.app.deliverer.dto;

import com.delivery.app.configs.validation.common.OnUpdate;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record DeliverUpdateLastLocationDTO(

        @NotNull(message = "DeliverId is mandatory", groups = { OnUpdate.class})
        UUID deliverId,

        @NotNull(message = "Latitude is mandatory", groups = { OnUpdate.class})
        Double latitude,

        @NotNull(message = "Longitude is mandatory", groups = { OnUpdate.class})
        Double longitude
) { }
