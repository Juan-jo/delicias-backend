package com.delivery.app.mobile.shopping.dto;

import lombok.Builder;

@Builder
public record DeliveryChargesDTO(
        double shipmentCost
) { }
