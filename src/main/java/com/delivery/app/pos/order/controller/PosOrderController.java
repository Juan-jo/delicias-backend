package com.delivery.app.pos.order.controller;

import com.delivery.app.configs.validation.common.OnCreate;
import com.delivery.app.configs.validation.common.OnFilter;
import com.delivery.app.pos.order.dto.CreateOrderRequestDTO;
import com.delivery.app.pos.order.dto.FilterOrdersRestaurantRequestDTO;
import com.delivery.app.pos.order.dto.FilterOrdersRestaurantResponseDTO;
import com.delivery.app.pos.order.dto.OrderDTO;
import com.delivery.app.pos.order.service.PosOrderService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(originPatterns = {"*"})
@RestController
@RequestMapping("/api/pos/order")
@RequiredArgsConstructor
@Validated
public class PosOrderController {

    private final PosOrderService posOrderService;

    @PostMapping
    @Validated(OnCreate.class)
    public ResponseEntity<Void> create(
            @Valid @RequestBody CreateOrderRequestDTO posOrderDTO) {

        posOrderService.create(posOrderDTO);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDTO> findById(
            @NotNull @PathVariable Integer orderId
    ) {

        return ResponseEntity.ok(
                posOrderService.findById(orderId)
        );
    }

    @PostMapping("/filter")
    @Validated(OnFilter.class)
    public ResponseEntity<Page<FilterOrdersRestaurantResponseDTO>> filter(
            @Valid @RequestBody FilterOrdersRestaurantRequestDTO requestDTO ) {

        return ResponseEntity.ok(
                posOrderService.filterRestaurant(requestDTO)
        );
    }
}
