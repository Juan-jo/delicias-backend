package com.delivery.app.mobile.app.controller;

import com.delivery.app.mobile.app.dto.MobileRestaurantDetailDTO;
import com.delivery.app.mobile.app.service.MobileRestaurantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(originPatterns = {"*"})
@RestController
@RequestMapping("/api/mobile/restaurant")
@RequiredArgsConstructor
@Validated
public class MobileRestaurantController {

    private final MobileRestaurantService mobileRestaurantService;

    @GetMapping("/{restaurantId}")
    public ResponseEntity<MobileRestaurantDetailDTO> findDetailById(
            @Valid @PathVariable Integer restaurantId
    ) {
        return ResponseEntity.ok(
                mobileRestaurantService.detail(restaurantId)
        );
    }


}
