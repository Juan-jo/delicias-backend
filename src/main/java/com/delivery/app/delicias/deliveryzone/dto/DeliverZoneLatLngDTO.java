package com.delivery.app.delicias.deliveryzone.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record DeliverZoneLatLngDTO(
        String name,
        List<List<Double>> coordinates,
        boolean active
) {
}
