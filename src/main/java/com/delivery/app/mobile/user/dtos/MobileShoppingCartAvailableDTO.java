package com.delivery.app.mobile.user.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record MobileShoppingCartAvailableDTO(
        UUID id,
        String restaurantName,
        String restaurantLogo,
        Integer lineCount
) { }
