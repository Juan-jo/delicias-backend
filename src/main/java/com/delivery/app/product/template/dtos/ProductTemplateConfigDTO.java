package com.delivery.app.product.template.dtos;

import com.delivery.app.configs.validation.common.OnUpdate;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ProductTemplateConfigDTO(
        @NotNull(message = "Id is mandatory", groups = { OnUpdate.class })
        Integer id,

        @NotNull(message = "name is mandatory", groups = { OnUpdate.class })
        String name,

        @NotNull(message = "Description is mandatory", groups = { OnUpdate.class })
        String description,

        @NotNull(message = "Category is mandatory", groups = { OnUpdate.class })
        Integer categId,

        Integer restaurantId,

        @NotNull(message = "list price is mandatory", groups = { OnUpdate.class })
        Double listPrice,

        @NotNull(message = "Id for sale is mandatory", groups = { OnUpdate.class })
        Boolean salesOk,

        @NotNull(message = "Active is mandatory", groups = { OnUpdate.class })
        Boolean active
) { }
