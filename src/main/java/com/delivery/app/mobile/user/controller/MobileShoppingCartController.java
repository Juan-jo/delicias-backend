package com.delivery.app.mobile.user.controller;

import com.delivery.app.mobile.user.dtos.MobileShoppingCartAvailableDTO;
import com.delivery.app.mobile.user.dtos.MobileShoppingCartDTO;
import com.delivery.app.mobile.user.service.MobileShoppingCartService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@CrossOrigin(originPatterns = {"*"})
@RestController
@RequestMapping("/api/v1/mobile/shopping")
@RequiredArgsConstructor
@Validated
public class MobileShoppingCartController {

    private final MobileShoppingCartService mobileShoppingCartService;

    @GetMapping("/{shoppingCartId}")
    public ResponseEntity<MobileShoppingCartDTO> findById(
            @NotNull @PathVariable UUID shoppingCartId
    ){

        return ResponseEntity.ok(
                mobileShoppingCartService.findById(shoppingCartId)
        );
    }

    @GetMapping
    public ResponseEntity<List<MobileShoppingCartAvailableDTO>> loadCartAvailable(){

        return ResponseEntity.ok(
                mobileShoppingCartService.availableShoppingCart()
        );
    }
}
