package com.delivery.app.product.category.dto;

import lombok.Builder;

@Builder
public record ProductCategoryParentOptionDTO(
        Integer id,
        String completeName
) {
}
