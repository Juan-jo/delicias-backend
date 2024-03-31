package com.delivery.app.product.category.dtos;

import com.delivery.app.configs.validation.common.OnCreate;
import com.delivery.app.configs.validation.common.OnUpdate;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;


@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ProductCategoryRecordDTO(
        @NotNull(message = "id is mandatory", groups = { OnUpdate.class})
        Integer id,

        @Size(max = 50, min = 1, message = "Not valid name. Must have minimum 1 chars or maximum 50 chars.", groups = { OnCreate.class, OnUpdate.class})
        @NotNull(message = "Name is mandatory", groups = { OnCreate.class, OnUpdate.class})
        String name,

        Integer parentId

) { }