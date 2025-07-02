package com.delivery.app.mobile.app.dto;

import com.delivery.app.configs.validation.common.OnCreate;
import jakarta.validation.constraints.NotNull;

public record MobileUserRegisterDTO(
        @NotNull(message = "name is mandatory", groups = {OnCreate.class})
        String name,

        @NotNull(message = "lastName is mandatory", groups = {OnCreate.class})
        String lastName,

        @NotNull(message = "email is mandatory", groups = {OnCreate.class})
        String email,

        @NotNull(message = "pwd is mandatory", groups = {OnCreate.class})
        String pwd,

        @NotNull(message = "latitude is mandatory", groups = {OnCreate.class})
        Double latitude,

        @NotNull(message = "longitude is mandatory", groups = {OnCreate.class})
        Double longitude
) { }
