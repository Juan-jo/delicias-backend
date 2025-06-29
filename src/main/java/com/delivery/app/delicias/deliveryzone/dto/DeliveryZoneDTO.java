package com.delivery.app.delicias.deliveryzone.dto;

import com.delivery.app.configs.validation.common.OnCreate;
import com.delivery.app.configs.validation.common.OnUpdate;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record DeliveryZoneDTO(
        @NotNull(message = "ID is mandatory", groups = {OnUpdate.class})
        Integer id,

        @NotNull(message = "name is mandatory", groups = {OnCreate.class, OnUpdate.class})
        String name,

        @NotNull(message = "hasMinimumAmount is mandatory", groups = {OnCreate.class, OnUpdate.class})
        boolean hasMinimumAmount,

        @NotNull(message = "minimumAmount is mandatory", groups = {OnCreate.class, OnUpdate.class})
        Integer minimumAmount,

        @NotNull(message = "active is mandatory", groups = {OnCreate.class, OnUpdate.class})
        boolean active,

        @Valid
        @NotNull(message = "position is mandatory", groups = {OnCreate.class, OnUpdate.class})
        Position position,

        @JsonFormat(pattern="dd/MM/yyyy HH:mm:ss")
        LocalDateTime createdAt,
        @JsonFormat(pattern="dd/MM/yyyy HH:mm:ss")
        LocalDateTime updatedAt
)
{

        @Builder
        public record Position(

                @NotNull(message = "latitude is mandatory", groups = {OnCreate.class, OnUpdate.class})
                Double latitude,

                @NotNull(message = "longitude is mandatory", groups = {OnCreate.class, OnUpdate.class})
                Double longitude,

                @NotNull(message = "radioPosition is mandatory", groups = {OnCreate.class, OnUpdate.class})
                Integer radioPosition
        ) { }
}
