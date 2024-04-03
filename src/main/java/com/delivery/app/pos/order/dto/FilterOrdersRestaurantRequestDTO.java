package com.delivery.app.pos.order.dto;

import com.delivery.app.configs.dto.BaseFilterDTO;
import com.delivery.app.pos.status.OrderStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class FilterOrdersRestaurantRequestDTO extends BaseFilterDTO {

    private OrderStatus status;

}
