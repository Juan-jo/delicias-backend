package com.delivery.app.pos.restaurant_kanban.service;


import com.delivery.app.pos.restaurant_kanban.dtos.DeliveryTrackingItem;
import com.delivery.app.pos.restaurant_kanban.repository.PosRestaurantKanbanRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class DeliveryTrackingService {

    private final PosRestaurantKanbanRepository posRestaurantKanbanRepository;

    public List<DeliveryTrackingItem> load() {

        return posRestaurantKanbanRepository.deliveryTracking().stream().map(
                r -> {

                    String wkt = r.get(2, String.class);

                    return new DeliveryTrackingItem(
                            r.get(0, String.class),
                            r.get(1, String.class),
                            getPosition(wkt));
                }
        ).toList();
    }

    public DeliveryTrackingItem.Position getPosition(String wkt) {
        if (wkt == null || !wkt.startsWith("POINT(") || !wkt.endsWith(")")) {
            throw new IllegalArgumentException("Invalid WKT format");
        }

        // Remove "POINT(" prefix and ")" suffix
        String coordinates = wkt.substring(6, wkt.length() - 1).trim();

        // Split coordinates by space
        String[] parts = coordinates.split(" ");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid WKT format");
        }

        double longitude;
        double latitude;

        try {
            longitude = Double.parseDouble(parts[0]);
            latitude = Double.parseDouble(parts[1]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid WKT format", e);
        }

        return new DeliveryTrackingItem.Position(latitude, longitude);
    }


}
