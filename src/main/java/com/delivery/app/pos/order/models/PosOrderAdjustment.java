package com.delivery.app.pos.order.models;

import com.delivery.app.pos.enums.AdjustmentType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PosOrderAdjustment {

    @Enumerated(EnumType.STRING)
    private AdjustmentType type;
    private String name;
    private Double amount;
    private String description;
}
