package com.delivery.app.pos.restaurant_kanban.dtos;

import com.delivery.app.pos.enums.OrderStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record PosKanbanOrderDTO(
        Integer kanbanId,
        Order order
) {

    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record Order(
            Integer orderId,
            @JsonFormat(pattern="dd/MM/yyyy HH:mm")
            LocalDateTime dateOrdered,
            Double amountTotal,
            boolean hasNote,
            String notes,
            List<OrderLine> lines,
            OrderDeliverer deliverer,
            OrderStatus status
    ) {

        @Builder
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public record OrderDeliverer(
                String name,
                String picture
        ) { }

    }

    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record OrderLine(
            String name,
            Integer qty,
            String picture,
            List<AttrValue> attrValues
    ) {


        @Builder
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public record AttrValue(
                String attrName,
                String attrValueName
        ) {


        }

    }



}
