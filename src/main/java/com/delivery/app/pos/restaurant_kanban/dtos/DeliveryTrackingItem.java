package com.delivery.app.pos.restaurant_kanban.dtos;

public record DeliveryTrackingItem(String type, String name, Position position) {

    public record Position(
            double latitude,
            double longitude
    ){}
}
