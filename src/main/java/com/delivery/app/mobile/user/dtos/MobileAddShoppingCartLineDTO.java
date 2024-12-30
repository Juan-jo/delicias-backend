package com.delivery.app.mobile.user.dtos;

import com.delivery.app.configs.validation.common.OnCreate;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public record MobileAddShoppingCartLineDTO(

        @NotNull(message = "Template Id is mandatory", groups = {OnCreate.class})
        Integer productTmplId,

        @NotNull(message = "Qty is mandatory", groups = {OnCreate.class})
        Integer qty,

        Set<Integer> attrValues
)
{ }
