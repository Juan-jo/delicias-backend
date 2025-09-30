package com.delivery.app.pos.order.dto;

import com.delicias.soft.services.core.common.OrderStatus;
import com.delivery.app.configs.dto.BaseFilterDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class FilterOrdersRestaurantRequestDTO extends BaseFilterDTO {

    private OrderStatus status;

}
