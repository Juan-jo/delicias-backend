package com.delivery.app.mobile.user.dtos;

import com.delivery.app.configs.validation.common.OnCreate;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

public record MobileUserCreateOrderDTO(
        @NotNull(message = "ShoppingCartId type is mandatory", groups = {OnCreate.class})
        UUID shoppingCartId,

        String notes

) {

}
