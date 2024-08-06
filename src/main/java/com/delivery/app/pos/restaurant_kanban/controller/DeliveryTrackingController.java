package com.delivery.app.pos.restaurant_kanban.controller;

import com.delivery.app.pos.restaurant_kanban.dtos.DeliveryTrackingItem;
import com.delivery.app.pos.restaurant_kanban.service.DeliveryTrackingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin(originPatterns = {"*"})
@RestController
@RequestMapping("/api/tracking")
@RequiredArgsConstructor
@Validated
public class DeliveryTrackingController {

    private final DeliveryTrackingService deliveryTrackingService;

    @GetMapping
    public ResponseEntity<List<DeliveryTrackingItem>> load() {

        return ResponseEntity.ok(
                deliveryTrackingService.load()
        );
    }

}
