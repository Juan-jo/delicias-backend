package com.delivery.app.product.template.dtos;

import com.delivery.app.configs.validation.common.OnCreate;
import com.delivery.app.configs.validation.common.OnUpdate;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ProductTemplateAttributeValueDTO(
        @NotNull(message = "id is mandatory", groups = { OnUpdate.class})
        Integer id,
        @NotNull(message = "productTmplId is mandatory", groups = { OnCreate.class})
        Integer productTmplId,

        @NotNull(message = "attributeId is mandatory", groups = { OnCreate.class})
        Integer attributeId,

        @NotNull(message = "name is mandatory", groups = { OnCreate.class})
        String name,

        @NotNull(message = "extraPrice is mandatory", groups = { OnCreate.class})
        Double extraPrice,

        Integer sequence
) {
}
