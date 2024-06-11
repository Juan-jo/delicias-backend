package com.delivery.app.product.category.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ProductCategoryRow(
        Integer id,
        String name,
        String completeName,

        @JsonFormat(pattern="dd/MM/yyyy HH:mm:ss")
        LocalDateTime createdAt,

        @JsonFormat(pattern="dd/MM/yyyy HH:mm:ss")
        LocalDateTime updatedAt
) {
}
