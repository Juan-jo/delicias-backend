package com.delivery.app.product.template.controller;

import com.delivery.app.configs.validation.common.OnCreate;
import com.delivery.app.product.template.dtos.ProductTemplateAttributeValueDTO;
import com.delivery.app.product.template.services.ProductTemplateAttributeValueRelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(originPatterns = {"*"})
@RestController
@RequestMapping("/api/product/rel/template-attribute-value")
@RequiredArgsConstructor
@Validated
public class ProductTemplateAttributeValueRelController {


    private final ProductTemplateAttributeValueRelService templateAttributeValueRelService;

    @PostMapping
    @Validated(OnCreate.class)
    public ResponseEntity<ProductTemplateAttributeValueDTO> create(
            @Valid @RequestBody ProductTemplateAttributeValueDTO attributeValueDTO
    ) {

        return ResponseEntity.ok(
                templateAttributeValueRelService.create(attributeValueDTO)
        );
    }

}
