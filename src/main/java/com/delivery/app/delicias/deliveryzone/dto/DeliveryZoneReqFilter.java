package com.delivery.app.delicias.deliveryzone.dto;

import com.delivery.app.configs.dto.BaseFilterDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeliveryZoneReqFilter extends BaseFilterDTO  {
    private String name;
}
