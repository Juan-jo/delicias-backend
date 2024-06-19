package com.delivery.app.product.template.dtos;

import lombok.Builder;

@Builder
public record ProductTmplAttributeRowDTO(
        Integer id,
        String attributeValue,
        Integer sequence,
        Double extraPrice
) { }
