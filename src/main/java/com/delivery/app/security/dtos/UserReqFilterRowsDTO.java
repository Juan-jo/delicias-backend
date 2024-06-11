package com.delivery.app.security.dtos;

import com.delivery.app.configs.validation.common.OnFilter;
import jakarta.validation.constraints.NotNull;

public record UserReqFilterRowsDTO(
        @NotNull(message = "The page number parameter is mandatory.", groups = { OnFilter.class })
        Integer page,

        @NotNull(message = "The elements number parameter is mandatory.", groups = { OnFilter.class })
        Integer size,

        String roleName,
        String userSearch
) { }
