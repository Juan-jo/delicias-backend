package com.delivery.app.product.template.dtos;

import com.delivery.app.configs.validation.common.OnCreate;
import com.delivery.app.configs.validation.common.OnUpdate;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record ProductTemplateAttributeDTO (

        @NotNull(message = "id is mandatory", groups = { OnUpdate.class })
        Integer id,

        @NotNull(message = "name is mandatory", groups = { OnCreate.class, OnUpdate.class })
        String name,

        @NotNull(message = "displayType is mandatory", groups = { OnCreate.class, OnUpdate.class})
        String displayType,

        @NotNull(message = "sequence is mandatory", groups = { OnCreate.class, OnUpdate.class})
        Integer sequence,

        Integer restaurantId
){ }
