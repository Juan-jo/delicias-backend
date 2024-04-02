package com.delivery.app.product.template.dtos;


import com.delivery.app.configs.validation.common.OnCreate;
import com.delivery.app.configs.validation.common.OnUpdate;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.Set;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ProductTemplateDTO(
        Integer id,
        String name,
        String description,
        Integer categId,
        Integer restaurantId,
        Boolean hasConfigurableAttributes,
        Double listPrice,
        Boolean salesOk,
        Boolean active,
        Set<Attribute> attributes,
        Set<AttributeValue> attributeValues
) {

    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record Attribute (
            Integer id,
            String name
    ) {}


    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record AttributeValue (
            Integer id,
            String name,
            Integer productTmplId,
            Integer attributeId,
            Double extraPrice,
            Integer sequence
    ) { }
}
