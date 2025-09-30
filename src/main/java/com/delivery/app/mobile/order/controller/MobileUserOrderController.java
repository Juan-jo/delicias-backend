package com.delivery.app.mobile.order.controller;

import com.delivery.app.configs.validation.common.OnCreate;
import com.delivery.app.mobile.user.dtos.MobileUserCreateOrderDTO;
import com.delivery.app.mobile.order.service.MobileUserCreateOrderService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(originPatterns = {"*"})
@RestController
@RequestMapping("/api/mobile/order")
@RequiredArgsConstructor
@Validated
public class MobileUserOrderController {

    private final MobileUserCreateOrderService mobileUserCreateOrderService;
    @PostMapping
    @Validated(OnCreate.class)
    public ResponseEntity<?> post(@Valid @RequestBody MobileUserCreateOrderDTO createOrderDTO) {

        mobileUserCreateOrderService.create(createOrderDTO);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{orderId}/create/supabase")
    public ResponseEntity<?> createSupabaseOrderTest(
            @NotNull @PathVariable Integer orderId) {

        mobileUserCreateOrderService.supabaseCreateOrder(orderId);

        return ResponseEntity.noContent().build();
    }

}
