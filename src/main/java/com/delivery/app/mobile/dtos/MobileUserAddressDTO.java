package com.delivery.app.mobile.dtos;


import com.delivery.app.configs.validation.common.OnCreate;
import com.delivery.app.configs.validation.common.OnUpdate;
import com.delivery.app.security.enums.UserAddressType;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record MobileUserAddressDTO(

        @NotNull(message = "The parameter is mandatory", groups = { OnUpdate.class })
        Integer id,

        @NotNull(message = "The parameter is mandatory", groups = { OnCreate.class, OnUpdate.class })
        UserAddressType addressType,

        @NotNull(message = "The parameter is mandatory", groups = { OnCreate.class, OnUpdate.class})
        String street,

        @NotNull(message = "The parameter is mandatory", groups = { OnCreate.class, OnUpdate.class})
        String address,

        @NotNull(message = "The parameter is mandatory", groups = { OnCreate.class, OnUpdate.class})
        Double latitude,

        @NotNull(message = "The parameter is mandatory", groups = { OnCreate.class, OnUpdate.class})
        Double longitude,

        @NotNull(message = "The parameter is mandatory", groups = { OnCreate.class, OnUpdate.class})
        String indications,

        String details,
        String companyName

) { }
