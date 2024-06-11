package com.delivery.app.security.dtos;

import com.delivery.app.configs.validation.common.OnCreate;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Setter;

@Builder
public record UserRegistrationRecordDTO(

        @Setter
        String id,

        @NotNull(message = "username is mandatory", groups = { OnCreate.class})
        String username,

        @NotNull(message = "email is mandatory", groups = { OnCreate.class})
        String email,

        @NotNull(message = "firstName is mandatory", groups = { OnCreate.class})
        String firstName,

        @NotNull(message = "lastName is mandatory", groups = { OnCreate.class})
        String lastName,

        @NotNull(message = "password is mandatory", groups = { OnCreate.class})
        String password,

        @NotNull(message = "roleName is mandatory", groups = { OnCreate.class})
        String roleName,

        Integer restaurantId
) { }
