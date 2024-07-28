package com.delivery.app.kafka.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public class KafkaRestaurantKanbanDTO {

    @JsonProperty("orderId")
    private Integer orderId;

    @JsonProperty("restaurantId")
    private Integer restaurantId;
}
