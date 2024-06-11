package com.delivery.app.restaurant.template.dto;

import com.delivery.app.configs.dto.BaseFilterDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RestaurantTemplateReqFilterRows extends BaseFilterDTO {
    private String name;
}
