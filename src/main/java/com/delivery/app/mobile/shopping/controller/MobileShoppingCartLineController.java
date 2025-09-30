package com.delivery.app.mobile.shopping.controller;

import com.delivery.app.configs.validation.common.OnCreate;
import com.delivery.app.configs.validation.common.OnUpdate;
import com.delivery.app.mobile.shopping.dto.MobileShoppingCartLineDTO;
import com.delivery.app.mobile.shopping.service.MobileShoppingCartLineService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@CrossOrigin(originPatterns = {"*"})
@RestController
@RequestMapping("/api/v1/mobile/shopping/line")
@RequiredArgsConstructor
@Validated
public class MobileShoppingCartLineController {

    private final MobileShoppingCartLineService mobileShoppingCartLineService;

    @PostMapping
    @Validated(OnCreate.class)
    public ResponseEntity<Void> addShippingLine(
            @Valid @RequestBody MobileShoppingCartLineDTO cartLineDTO
    ) {

        mobileShoppingCartLineService.addShoppingCartLine(cartLineDTO);

        return ResponseEntity.ok().build();
    }

    @PutMapping
    @Validated(OnUpdate.class)
    public ResponseEntity<Void> updateShippingLine(
            @Valid @RequestBody MobileShoppingCartLineDTO cartLineDTO
    ) {

        mobileShoppingCartLineService.updateShoppingCartLine(cartLineDTO);

        return ResponseEntity.ok().build();
    }


    @DeleteMapping("/{shoppingCartLineId}")
    public ResponseEntity<Void> deleteShippingLine(
            @NotNull @PathVariable UUID shoppingCartLineId
    ) {

        mobileShoppingCartLineService.deleteShoppingCartLine(shoppingCartLineId);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/{shoppingCartLineId}")
    public ResponseEntity<MobileShoppingCartLineDTO> getShippingLine(
            @NotNull @PathVariable UUID shoppingCartLineId
    ) {

        return ResponseEntity.ok(
                mobileShoppingCartLineService.getShoppingLine(shoppingCartLineId)
        );
    }


}
