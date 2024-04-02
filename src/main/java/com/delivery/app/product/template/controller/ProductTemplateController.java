package com.delivery.app.product.template.controller;

import com.delivery.app.product.template.dtos.ProductTemplateDTO;
import com.delivery.app.product.template.services.ProductTemplateService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(originPatterns = {"*"})
@RestController
@RequestMapping("/api/product/template")
@RequiredArgsConstructor
@Validated
public class ProductTemplateController {

    private final ProductTemplateService productTemplateService;
    @GetMapping("/{tmplId}")
    public ResponseEntity<ProductTemplateDTO> getById(
            @NotNull @PathVariable Integer tmplId) {

        return ResponseEntity.ok(
                productTemplateService.findById(tmplId)
        );
    }
}
