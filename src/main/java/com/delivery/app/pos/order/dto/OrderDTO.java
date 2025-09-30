package com.delivery.app.pos.order.dto;

import com.delicias.soft.services.core.common.OrderStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record OrderDTO(
        Integer id,
        OrderStatus status
)
{ }
