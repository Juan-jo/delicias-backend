package com.delivery.app.pos.kanban.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record PosRestaurantKanbanDTO(
        Integer id,
        String label,
        List<KanbanList> children) {

    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record KanbanList(
            String id,
            //String label,
            List<Order> children) {}

    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record Order(
            Integer kanbanId,
            Integer id,
            @JsonFormat(pattern="HH:mm")
            LocalDateTime dateOrdered,
            Double amountTotal,
            boolean hasNote,
            List<Product> products) {}

    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record Product(
            String name,
            Integer qty,
            String attrValuesDesc) { }
}
