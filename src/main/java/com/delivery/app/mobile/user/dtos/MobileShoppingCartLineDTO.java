package com.delivery.app.mobile.user.dtos;

import com.delivery.app.configs.validation.common.OnCreate;
import com.delivery.app.configs.validation.common.OnUpdate;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.Set;
import java.util.UUID;

@Builder
public record MobileShoppingCartLineDTO(

        @NotNull(message = "Id is mandatory", groups = {OnUpdate.class})
        UUID id,

        @NotNull(message = "Template Id is mandatory", groups = {OnCreate.class})
        Integer productTmplId,

        @NotNull(message = "Qty is mandatory", groups = {OnCreate.class, OnUpdate.class})
        Integer qty,

        Set<Integer> attrValues
)
{ }
