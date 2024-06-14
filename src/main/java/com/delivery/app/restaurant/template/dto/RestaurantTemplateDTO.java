package com.delivery.app.restaurant.template.dto;

import com.delivery.app.configs.validation.common.OnCreate;
import com.delivery.app.configs.validation.common.OnUpdate;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record RestaurantTemplateDTO(
        @NotNull(message = "id is mandatory", groups = { OnUpdate.class})
        Integer id,

        @Size(max = 250, min = 1, message = "Not valid name. Must have minimum 1 chars or maximum 250 chars.", groups = { OnCreate.class, OnUpdate.class})
        @NotNull(message = "Name is mandatory", groups = { OnCreate.class, OnUpdate.class})
        String name,

        @Size(max = 500, message = "Not valid name. Must have minimum 1 chars or maximum 500 chars.", groups = { OnCreate.class, OnUpdate.class})
        String description,

        @Size(max = 15, message = "Not valid phone. Must have minimum 1 chars or maximum 500 chars.", groups = { OnCreate.class, OnUpdate.class})
        String phone
) { }
