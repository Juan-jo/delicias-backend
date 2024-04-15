package com.delivery.app.pos.order.dto;

import com.delivery.app.configs.validation.common.OnCreate;
import com.delivery.app.pos.enums.PaymentType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public record CreateOrderRequestDTO(
        @NotNull(message = "Payment type is mandatory", groups = {OnCreate.class})
        PaymentType paymentType,

        @Valid
        @NotNull(message = "Product templates is mandatory", groups = {OnCreate.class})
        Set<RequestOrderProductTemplate> productTemplates
) {

        public record RequestOrderProductTemplate(
                @NotNull(message = "Template id is mandatory", groups = {OnCreate.class})
                Integer id,
                @NotNull(message = "Quantity is mandatory", groups = {OnCreate.class})
                Integer qty,
                Set<Integer> attributeValues

        ) {}

}
