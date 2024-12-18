package com.delivery.app.mobile.deliverer.controller;

import com.delivery.app.configs.validation.common.OnUpdate;
import com.delivery.app.mobile.deliverer.dto.DeliveryOrderLastPosition;
import com.delivery.app.mobile.deliverer.dto.MobileDeliveryOrderDetailDTO;
import com.delivery.app.mobile.deliverer.service.MobileDeliveryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(originPatterns = {"*"})
@RestController
@RequestMapping("/api/v1/mobile/r/order")
@RequiredArgsConstructor
@Validated
public class MobileDeliveryController {

    private final MobileDeliveryService mobileDeliveryService;

    @GetMapping("/{orderId}")
    public ResponseEntity<MobileDeliveryOrderDetailDTO> order(
            @Valid @PathVariable Integer orderId
    ) {

        return ResponseEntity.ok(
                mobileDeliveryService.detail(orderId)
        );
    }

    @Async
    @PutMapping("/pgs/tracking")
    @Validated(OnUpdate.class)
    public ResponseEntity<?> updateLastPosition(
            @Valid @RequestBody DeliveryOrderLastPosition orderLastPosition
    ) {

        mobileDeliveryService.updateGpsTracking(orderLastPosition);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{orderId}/start")
    @Validated(OnUpdate.class)
    public ResponseEntity<?> start(
            @Valid @PathVariable Integer orderId
    ) {

        mobileDeliveryService.roadToStore(orderId);

        return ResponseEntity.noContent().build();
    }

}
