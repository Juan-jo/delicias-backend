package com.delivery.app.delicias.general.controller;


import com.delivery.app.configs.validation.common.OnUpdate;
import com.delivery.app.delicias.general.dto.MobileConfigDTO;
import com.delivery.app.delicias.general.service.MobileConfigService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(originPatterns = {"*"})
@RestController
@RequestMapping("/api/mobile/general")
@RequiredArgsConstructor
@Validated
public class MobileGeneralController {

    private final MobileConfigService mobileConfigService;

    @GetMapping
    public ResponseEntity<MobileConfigDTO> findById() {
        return ResponseEntity.ok(
                mobileConfigService.mobileConfig()
        );
    }

    @PutMapping
    @Validated(OnUpdate.class)
    public ResponseEntity<Void> update(@Valid @RequestBody MobileConfigDTO configDTO) {

        mobileConfigService.update(configDTO);

        return ResponseEntity.ok().build();
    }
}
