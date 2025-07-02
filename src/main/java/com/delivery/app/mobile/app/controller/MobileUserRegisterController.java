package com.delivery.app.mobile.app.controller;

import com.delivery.app.configs.validation.common.OnCreate;
import com.delivery.app.mobile.app.dto.MobileUserRegisterDTO;
import com.delivery.app.mobile.app.service.MobileUserRegisterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin(originPatterns = {"*"})
@RestController
@RequestMapping("/api/mobile/userRegister")
@RequiredArgsConstructor
@Validated
public class MobileUserRegisterController {

    private final MobileUserRegisterService mobileUserRegisterService;

    @PostMapping
    @Validated(OnCreate.class)
    public ResponseEntity<Map<String, Object>> register(
            @RequestBody @Valid MobileUserRegisterDTO mobileUserRegisterDTO
    ) {

        return ResponseEntity.ok().body(
                mobileUserRegisterService.register(mobileUserRegisterDTO)
        );

    }
}
