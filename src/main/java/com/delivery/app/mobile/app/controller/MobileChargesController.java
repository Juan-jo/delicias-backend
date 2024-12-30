package com.delivery.app.mobile.app.controller;

import com.delivery.app.mobile.app.dto.ChargesDTO;
import com.delivery.app.mobile.app.service.MobileConfigService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.regex.Pattern;

@CrossOrigin(originPatterns = {"*"})
@RestController
@RequestMapping("/api/v1/mobile/charges")
@RequiredArgsConstructor
@Validated
public class MobileChargesController {

    private final MobileConfigService mobileConfigService;

    @GetMapping("/{restaurantId}")
    public ResponseEntity<ChargesDTO> load(
            @Valid @PathVariable Integer restaurantId,
            @RequestHeader("LatLng") String destination) {

        String[] latLng = destination.split(Pattern.quote("|"));

        double latitude = Double.parseDouble(latLng[0]);
        double longitude = Double.parseDouble(latLng[1]);

        return ResponseEntity.ok(
                mobileConfigService.loadCharges(restaurantId, latitude, longitude)
        );
    }

    @GetMapping("/restaurant/{restaurantId}/address/{userAddressId}")
    public ResponseEntity<ChargesDTO> load(
            @Valid @PathVariable Integer restaurantId,
            @Valid @PathVariable Integer userAddressId
    ) {

        return ResponseEntity.ok(
                mobileConfigService.loadCharges(restaurantId, userAddressId)
        );
    }
}
