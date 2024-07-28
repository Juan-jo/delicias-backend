package com.delivery.app.mobile.controller;

import com.delivery.app.configs.validation.common.OnCreate;
import com.delivery.app.mobile.dtos.MobileCreateOrderDTO;
import com.delivery.app.mobile.service.MobileCreateOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(originPatterns = {"*"})
@RestController
@RequestMapping("/api/mobile/order")
@RequiredArgsConstructor
@Validated
public class MobileOrderController {

    private final MobileCreateOrderService mobileCreateOrderService;
    @PostMapping
    @Validated(OnCreate.class)
    public ResponseEntity<?> post(@Valid @RequestBody MobileCreateOrderDTO createOrderDTO) {




        mobileCreateOrderService.create(createOrderDTO);

        return ResponseEntity.noContent().build();
    }


}
