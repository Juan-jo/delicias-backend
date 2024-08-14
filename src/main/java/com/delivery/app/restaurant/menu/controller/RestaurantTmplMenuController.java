package com.delivery.app.restaurant.menu.controller;

import com.delivery.app.restaurant.menu.service.RestaurantTmplMenuService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(originPatterns = {"*"})
@RestController
@RequestMapping("/api/restaurant/menu")
@RequiredArgsConstructor
@Validated
public class RestaurantTmplMenuController {

    private final RestaurantTmplMenuService restaurantTmplMenuService;

    @DeleteMapping("/{menuId}")
    public ResponseEntity<?> deleteById(
            @NotNull @PathVariable Integer menuId
    ) {


        restaurantTmplMenuService.deleteByMenuId(menuId);
        return ResponseEntity.noContent().build();
    }

}
