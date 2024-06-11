package com.delivery.app.product.category.dto;

import com.delivery.app.configs.dto.BaseFilterDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductCategoryReqFilterRows extends BaseFilterDTO{
    private String name;
}
