package com.delivery.app.product.template.dtos;

import com.delivery.app.configs.validation.common.OnCreate;
import com.delivery.app.configs.validation.common.OnUpdate;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record ProductTemplateRecordDTO(

        @NotNull(message = "Id is mandatory", groups = { OnUpdate.class })
        Integer id,

        @Size(max = 50, min = 1, message = "Not valid name. Must have minimum 1 chars or maximum 50 chars.", groups = { OnCreate.class, OnUpdate.class})
        @NotNull(message = "Name is mandatory", groups = { OnCreate.class, OnUpdate.class})
        String name,

        @NotNull(message = "Category is mandatory", groups = { OnCreate.class, OnUpdate.class})
        Integer categId,

        Integer restaurantId
) { }
