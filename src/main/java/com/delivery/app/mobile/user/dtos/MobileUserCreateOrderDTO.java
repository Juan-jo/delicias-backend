package com.delivery.app.mobile.user.dtos;

import com.delivery.app.configs.validation.common.OnCreate;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record MobileUserCreateOrderDTO(

        @NotNull(message = "The parameter is mandatory.", groups = { OnCreate.class })
        Integer restaurantId,

        @NotNull(message = "The parameter is mandatory.", groups = { OnCreate.class })
        Double subtotal,

        @NotNull(message = "The parameter is mandatory.", groups = { OnCreate.class })
        Double discount,

        @NotNull(message = "The parameter is mandatory.", groups = { OnCreate.class })
        Double costService,

        @NotNull(message = "The parameter is mandatory.", groups = { OnCreate.class })
        Double total,

        @Size(max = 250, message = "Not valid name. Must have minimum 1 chars or maximum 150 chars.", groups = { OnCreate.class})
        String notes,

        @Valid
        @NotEmpty(message = "The parameter is mandatory.", groups = { OnCreate.class })
        List<ProductTmpl> productTmpl
) {

    public record ProductTmpl(

            @NotNull(message = "The parameter is mandatory.", groups = { OnCreate.class })
            Integer id,

            @NotNull(message = "The parameter is mandatory.", groups = { OnCreate.class })
            Integer qty,

            List<AttrValue> attrValues
    ) { }


    public record AttrValue(
            @NotNull(message = "The parameter is mandatory.", groups = { OnCreate.class })
            Integer id
    ) { }
}
