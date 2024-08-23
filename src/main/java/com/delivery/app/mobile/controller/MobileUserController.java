package com.delivery.app.mobile.controller;

import com.delivery.app.configs.validation.common.OnCreate;
import com.delivery.app.mobile.dtos.MobileGeocodingDTO;
import com.delivery.app.mobile.dtos.MobileUserAddressDTO;
import com.delivery.app.mobile.service.MobileUserService;
import com.delivery.app.restaurant.template.dto.RestaurantTemplateDTO;
import com.delivery.app.security.dtos.UserAddressDTO;
import com.google.maps.errors.ApiException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

@CrossOrigin(originPatterns = {"*"})
@RestController
@RequestMapping("/api/v1/mobile/user")
@RequiredArgsConstructor
@Validated
public class MobileUserController {


    private final MobileUserService mobileUserService;

    @GetMapping("/search-nearby")
    public ResponseEntity<Set<MobileGeocodingDTO>> searchNearby(
            @RequestHeader("LatLng") String destination
    ) throws IOException, InterruptedException, ApiException {

        String[] latLng = destination.split(Pattern.quote("|"));

        double latitude = Double.parseDouble(latLng[0]);
        double longitude = Double.parseDouble(latLng[1]);

        return ResponseEntity.ok(
                mobileUserService.searchNearby(latitude, longitude)
        );
    }

    @PostMapping("/add-address")
    @Validated(OnCreate.class)
    public ResponseEntity<UserAddressDTO> addAddress(
            @Valid @RequestBody MobileUserAddressDTO userAddressDTO
    ) {

        return ResponseEntity.ok(
                mobileUserService.addAddress(userAddressDTO)
        );
    }

}
