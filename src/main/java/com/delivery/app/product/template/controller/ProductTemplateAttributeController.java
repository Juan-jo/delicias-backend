package com.delivery.app.product.template.controller;

import com.delivery.app.configs.validation.common.OnCreate;
import com.delivery.app.configs.validation.common.OnUpdate;
import com.delivery.app.product.template.dtos.ProductTemplateAttributeDTO;
import com.delivery.app.product.template.services.ProductTemplateAttributeService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(originPatterns = {"*"})
@RestController
@RequestMapping("/api/product/attribute")
@RequiredArgsConstructor
@Validated
public class ProductTemplateAttributeController {

    private final ProductTemplateAttributeService productTemplateAttributeService;

    @GetMapping("/res/{restaurantId}")
    public ResponseEntity<List<ProductTemplateAttributeDTO>> getById(
            @NotNull @PathVariable Integer restaurantId) {

        return ResponseEntity.ok(
                productTemplateAttributeService.getAttributesByRestaurantId(restaurantId)
        );
    }

    @PostMapping
    @Validated(OnCreate.class)
    public ResponseEntity<ProductTemplateAttributeDTO> create(
            @Valid @RequestBody ProductTemplateAttributeDTO attributeDTO) {

        return ResponseEntity.ok(
                productTemplateAttributeService.create(attributeDTO)
        );
    }

    @PutMapping
    @Validated(OnUpdate.class)
    public ResponseEntity<?> update(
            @Valid @RequestBody ProductTemplateAttributeDTO attributeDTO) {

        productTemplateAttributeService.update(attributeDTO);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(
            @NotNull @PathVariable Integer id) {

        productTemplateAttributeService.delete(id);

        return ResponseEntity.noContent().build();
    }

}
