package com.delivery.app.pos.order.dto;

import com.delivery.app.configs.validation.common.OnCreate;
import com.delivery.app.pos.enums.PaymentType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.Set;
import java.util.UUID;

public record CreateOrderRequestDTO(


        PaymentType paymentType,

        Set<RequestOrderProductTemplate> productTemplates,

        @NotNull(message = "ShoppingCartId type is mandatory", groups = {OnCreate.class})
        UUID shoppingCartId
) {

        public record RequestOrderProductTemplate(
                @NotNull(message = "Template id is mandatory", groups = {OnCreate.class})
                Integer id,
                @NotNull(message = "Quantity is mandatory", groups = {OnCreate.class})
                Integer qty,
                Set<Integer> attributeValues

        ) {}

}
