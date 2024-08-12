package com.delivery.app.mobile.controller;

import com.delivery.app.configs.validation.common.OnFilter;
import com.delivery.app.mobile.dtos.ProductFilterRequestDTO;
import com.delivery.app.mobile.dtos.MobileProductTmplDetailDTO;
import com.delivery.app.mobile.dtos.ProductTemplateItemDTO;
import com.delivery.app.mobile.service.MobileProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(originPatterns = {"*"})
@RestController
@RequestMapping("/api/mobile/product")
@RequiredArgsConstructor
@Validated
public class MobileProductController {

    private final MobileProductService mobileProductService;

    @PostMapping
    @Validated(OnFilter.class)
    public ResponseEntity<Page<ProductTemplateItemDTO>> load(
           @Valid @RequestBody ProductFilterRequestDTO filterRequestDTO
    ) {
        return ResponseEntity.ok(
                mobileProductService.loadProductTemplate(filterRequestDTO)
        );
    }

    @GetMapping("/{tmplId}")
    public ResponseEntity<MobileProductTmplDetailDTO> findById(
            @Valid @PathVariable Integer tmplId
    ) {
        return ResponseEntity.ok(
                mobileProductService.detail(tmplId)
        );
    }
}
