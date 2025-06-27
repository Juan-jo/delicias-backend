package com.delivery.app.restaurant.config.dto;

import com.delivery.app.configs.validation.common.OnUpdate;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@Builder
public record RestaurantTmplConfigDTO(

        @NotNull(message = "id is mandatory", groups = { OnUpdate.class})
        Integer restaurantId,

        String restaurantName,

        String address,

        Double latitude,

        Double longitude,

        String imageCover,

        String imageLogo,

        @Valid
        @NotEmpty(message = "The parameter is mandatory.", groups = { OnUpdate.class })
        List<Schedule> schedules,

        List<Menu> menus,

        List<Integer> recommended,

        Map<String, List<ProductTmpl>> products
) {

    @Builder
    public record Schedule(

            @NotNull(message = "The parameter is mandatory", groups = { OnUpdate.class})
            Integer id,

            @NotNull(message = "The parameter is mandatory", groups = { OnUpdate.class})
            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
            LocalTime startTime,

            @NotNull(message = "The parameter is mandatory", groups = { OnUpdate.class})
            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
            LocalTime endTime,

            @NotNull(message = "The parameter is mandatory", groups = { OnUpdate.class})
            @Enumerated(EnumType.STRING)
            DayOfWeek dayOfWeek,

            boolean available
    ) { }

    @Builder
    public record Menu(

            Integer id,

            @NotNull(message = "The parameter is mandatory", groups = { OnUpdate.class})
            String name,

            @NotNull(message = "The parameter is mandatory", groups = { OnUpdate.class})
            Boolean available,

            @NotNull(message = "The parameter is mandatory", groups = { OnUpdate.class})
            Integer sequence,

            @NotEmpty(message = "The parameter is mandatory.", groups = { OnUpdate.class })
            List<Integer> productsTmpl
    ) {}


    @Builder
    public record ProductTmpl(
            Integer id,
            String categ,
            String name,
            String picture
    ) {

    }
}
