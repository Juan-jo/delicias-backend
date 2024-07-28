package com.delivery.app.product.template.dtos;

import com.delivery.app.configs.validation.common.OnUpdate;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ProductTemplateConfigDTO(
        @NotNull(message = "Id is mandatory", groups = { OnUpdate.class })
        Integer id,

        @Size(max = 50, min = 1, message = "Not valid name. Must have minimum 1 chars or maximum 50 chars.", groups = { OnUpdate.class})
        @NotNull(message = "name is mandatory", groups = { OnUpdate.class })
        String name,

        String description,

        @NotNull(message = "Category is mandatory", groups = { OnUpdate.class })
        Integer categId,

        Integer restaurantId,

        @NotNull(message = "list price is mandatory", groups = { OnUpdate.class })
        double listPrice,

        @NotNull(message = "Id for sale is mandatory", groups = { OnUpdate.class })
        boolean salesOk,

        @NotNull(message = "Active is mandatory", groups = { OnUpdate.class })
        boolean active
) { }
