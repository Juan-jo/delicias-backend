package com.delivery.app.pos.kanban.dtos;

import com.delicias.soft.services.core.common.OrderStatus;
import com.delivery.app.configs.validation.common.OnUpdate;
import jakarta.validation.constraints.NotNull;


public record UpdatePosRestaurantKanbanDTO(

        @NotNull(message = "Id is mandatory", groups = { OnUpdate.class})
        Integer orderId,

        @NotNull(message = "Status is mandatory", groups = { OnUpdate.class})
        OrderStatus status
) { }
