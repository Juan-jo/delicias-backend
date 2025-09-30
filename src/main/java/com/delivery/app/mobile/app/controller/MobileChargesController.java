package com.delivery.app.mobile.app.controller;

import com.delivery.app.mobile.shopping.dto.DeliveryChargesDTO;
import com.delivery.app.mobile.shopping.service.MobileShoppingCartChargesService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(originPatterns = {"*"})
@RestController
@RequestMapping("/api/v1/mobile/charges")
@RequiredArgsConstructor
@Validated
public class MobileChargesController {

    private final MobileShoppingCartChargesService mobileShoppingCartChargesService;


    @GetMapping("/restaurant/{restaurantId}/address/{userAddressId}")
    public ResponseEntity<DeliveryChargesDTO> load(
            @Valid @PathVariable Integer restaurantId,
            @Valid @PathVariable Integer userAddressId
    ) {

        return ResponseEntity.ok(
                mobileShoppingCartChargesService.getDeliveryCharges(restaurantId, userAddressId)
        );
    }
}
