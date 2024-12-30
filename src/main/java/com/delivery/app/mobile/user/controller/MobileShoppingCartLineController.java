package com.delivery.app.mobile.user.controller;

import com.delivery.app.configs.validation.common.OnCreate;
import com.delivery.app.mobile.user.dtos.MobileAddShoppingCartLineDTO;
import com.delivery.app.mobile.user.service.MobileShoppingCartLineService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(originPatterns = {"*"})
@RestController
@RequestMapping("/api/v1/mobile/shopping/line")
@RequiredArgsConstructor
@Validated
public class MobileShoppingCartLineController {

    private final MobileShoppingCartLineService mobileShoppingCartLineService;

    @PostMapping
    @Validated(OnCreate.class)
    public ResponseEntity<Void> addAddress(
            @Valid @RequestBody MobileAddShoppingCartLineDTO cartLineDTO
    ) {

        mobileShoppingCartLineService.addShoppingCartLine(cartLineDTO);

        return ResponseEntity.ok().build();
    }

}
