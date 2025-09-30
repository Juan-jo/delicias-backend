package com.delivery.app.pos.kanban.dtos;

import com.delivery.app.configs.validation.common.OnUpdate;
import com.delivery.app.pos.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;


public record UpdatePosRestaurantKanbanDTO(

        @NotNull(message = "Id is mandatory", groups = { OnUpdate.class})
        Integer orderId,

        @NotNull(message = "Status is mandatory", groups = { OnUpdate.class})
        OrderStatus status
) { }
