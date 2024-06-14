package com.delivery.app.security.dtos;

import com.delivery.app.configs.validation.common.OnCreate;
import com.delivery.app.configs.validation.common.OnUpdate;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record UserDTO(

        @NotNull(message = "id is mandatory", groups = { OnUpdate.class})
        @Setter
        String id,

        @NotNull(message = "username is mandatory", groups = { OnCreate.class})
        String username,

        @NotNull(message = "email is mandatory", groups = { OnCreate.class, OnUpdate.class})
        String email,

        @NotNull(message = "firstName is mandatory", groups = { OnCreate.class, OnUpdate.class})
        String firstName,

        @NotNull(message = "lastName is mandatory", groups = { OnCreate.class, OnUpdate.class})
        String lastName,

        @NotNull(message = "password is mandatory", groups = { OnCreate.class})
        String password,

        @NotNull(message = "roleName is mandatory", groups = { OnCreate.class, OnUpdate.class})
        String roleName,

        Integer restaurantId
) { }
