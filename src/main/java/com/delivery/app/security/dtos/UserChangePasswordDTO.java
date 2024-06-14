package com.delivery.app.security.dtos;


import com.delivery.app.configs.validation.common.OnUpdate;
import jakarta.validation.constraints.NotNull;

public record UserChangePasswordDTO(
        @NotNull(message = "id is mandatory", groups = { OnUpdate.class})
        String id,

        @NotNull(message = "password is mandatory", groups = { OnUpdate.class})
        String password
) { }
