package com.delivery.app.restaurant.template.dto;

import com.delivery.app.configs.validation.common.OnUpdate;
import jakarta.validation.constraints.NotNull;

public record RestaurantTmplEnabledMobileDTO(
        @NotNull(message = "id is mandatory", groups = { OnUpdate.class})
        Integer id,
        @NotNull(message = "enabledMobile is mandatory", groups = { OnUpdate.class})
        boolean enabledMobile
) { }
