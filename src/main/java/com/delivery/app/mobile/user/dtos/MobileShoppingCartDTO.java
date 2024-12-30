package com.delivery.app.mobile.user.dtos;

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
        double shipmentCost,
        double total
) {

    @Builder
    public record ShoppingLine(
            UUID id,
            String productTmplName,
            String productTmplDescription,
            Integer qty,
            double price
    ) {}

    @Builder
    public record ShoppingCartDeliveryAddress(
            Integer id,
            String name,
            String address
    ) {}

}
