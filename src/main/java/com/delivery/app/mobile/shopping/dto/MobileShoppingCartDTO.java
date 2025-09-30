package com.delivery.app.mobile.shopping.dto;

import com.delivery.app.pos.enums.AdjustmentType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.util.List;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record MobileShoppingCartDTO(
        UUID id,
        boolean hasDeliveryAddress,
        ShoppingCartDeliveryAddress deliveryAddress,
        List<ShoppingLine> shoppingLines,
        double subtotal,
        double total,

        List<ShoppingCharge> charges,
        Integer restaurantId
) {

    @Builder
    public record ShoppingLine(
            UUID id,
            Integer productTmplId,
            String productTmplName,
            String productTmplDescription,
            Integer qty,
            double priceUnit,
            double priceTotal,
            List<Integer> attrValuesAdded
    ) {}

    @Builder
    public record ShoppingCartDeliveryAddress(
            Integer id,
            String name,
            String address,
            String icon,
            double latitude,
            double longitude
    ) {}

    @Builder
    public record ShoppingCharge(
            String name,
            AdjustmentType adjustmentType,
            double amount
    ) { }
}
