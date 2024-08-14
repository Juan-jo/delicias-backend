package com.delivery.app.restaurant.config.controller;

import com.delivery.app.configs.validation.common.OnUpdate;
import com.delivery.app.restaurant.config.dto.RestaurantTmplConfigDTO;
import com.delivery.app.restaurant.config.dto.RestaurantTmplLocationUpdateDTO;
import com.delivery.app.restaurant.config.service.RestaurantTmplConfigService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(originPatterns = {"*"})
@RestController
@RequestMapping("/api/restaurant/config")
@RequiredArgsConstructor
@Validated
public class RestaurantTmplConfigController {

    private final RestaurantTmplConfigService restaurantTmplConfigService;


    @GetMapping("/{restaurantTmplId}")
    public ResponseEntity<RestaurantTmplConfigDTO> getById(
            @NotNull @PathVariable Integer restaurantTmplId
    ) {

        return ResponseEntity.ok(
                restaurantTmplConfigService.loadConfig(restaurantTmplId)
        );
    }

    @PutMapping
    @Validated(OnUpdate.class)
    public ResponseEntity<?> update(
            @Valid @RequestBody RestaurantTmplConfigDTO tmplConfigDTO
    ) {

        restaurantTmplConfigService.update(tmplConfigDTO);
        return ResponseEntity.noContent().build();
    }


    @PutMapping("/location")
    @Validated(OnUpdate.class)
    public ResponseEntity<?> location(
            @Valid @RequestBody RestaurantTmplLocationUpdateDTO restaurantTmplLocationUpdateDTO
    ) {

        restaurantTmplConfigService.updateLocation(restaurantTmplLocationUpdateDTO);
        return ResponseEntity.noContent().build();
    }
}
