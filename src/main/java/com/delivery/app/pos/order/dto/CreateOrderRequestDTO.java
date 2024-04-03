package com.delivery.app.pos.order.dto;

import com.delivery.app.configs.validation.common.OnCreate;

import javax.validation.constraints.NotEmpty;
import java.util.Set;

public record CreateOrderRequestDTO(
        @NotEmpty(message = "tmplId list cannot be empty.", groups = {OnCreate.class})
        Set<Integer> tmplId
) { }
