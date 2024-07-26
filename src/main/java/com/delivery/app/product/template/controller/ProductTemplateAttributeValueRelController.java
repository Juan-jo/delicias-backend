package com.delivery.app.product.template.controller;

import com.delivery.app.configs.validation.common.OnCreate;
import com.delivery.app.configs.validation.common.OnUpdate;
import com.delivery.app.product.template.dtos.ProductTemplateAttributeValueDTO;
import com.delivery.app.product.template.services.ProductTemplateAttributeValueRelService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
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
    public ResponseEntity<?> create(
            @Valid @RequestBody ProductTemplateAttributeValueDTO attributeValueDTO
    ) {
        templateAttributeValueRelService.create(attributeValueDTO);

        return ResponseEntity.noContent().build();
    }

    @PutMapping
    @Validated(OnUpdate.class)
    public ResponseEntity<?> update(
            @Valid @RequestBody ProductTemplateAttributeValueDTO attributeValueDTO
    ) {
        templateAttributeValueRelService.update(attributeValueDTO);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @Validated(OnUpdate.class)
    public ResponseEntity<ProductTemplateAttributeValueDTO> update(
            @NotNull @PathVariable Integer id
    ) {
        return ResponseEntity.ok(
                templateAttributeValueRelService.findById(id)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(
            @NotNull @PathVariable Integer id
    ) {

        templateAttributeValueRelService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
