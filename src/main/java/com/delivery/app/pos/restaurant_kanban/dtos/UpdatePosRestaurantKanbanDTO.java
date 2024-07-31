package com.delivery.app.pos.restaurant_kanban.dtos;

import com.delivery.app.configs.validation.common.OnUpdate;
import com.delivery.app.pos.enums.KanbanStatus;
import jakarta.validation.constraints.NotNull;


public record UpdatePosRestaurantKanbanDTO(

        @NotNull(message = "Id is mandatory", groups = { OnUpdate.class})
        Integer id,

        @NotNull(message = "Status is mandatory", groups = { OnUpdate.class})
        KanbanStatus status
) { }
