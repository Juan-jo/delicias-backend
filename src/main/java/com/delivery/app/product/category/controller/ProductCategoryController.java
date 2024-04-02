package com.delivery.app.product.category.controller;

import com.delivery.app.configs.validation.common.OnCreate;
import com.delivery.app.configs.validation.common.OnUpdate;
import com.delivery.app.product.category.dto.ProductCategoryRecordDTO;
import com.delivery.app.product.category.service.ProductCategoryService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@CrossOrigin(originPatterns = {"*"})
@RestController
@RequestMapping("/api/product/category")
@RequiredArgsConstructor
@Validated
public class ProductCategoryController {

    private final ProductCategoryService productCategoryService;

    @GetMapping("/user")
    public ResponseEntity<String> getUser(Principal principal) {
        JwtAuthenticationToken token = (JwtAuthenticationToken) principal;
        String userName = (String) token.getTokenAttributes().get("name");
        String userEmail = (String) token.getTokenAttributes().get("email");

        return ResponseEntity.ok("Hello User \nUser Name : " + userName + "\nUser Email : " + userEmail);
    }

    @PostMapping
    @Validated(OnCreate.class)
    public ResponseEntity<ProductCategoryRecordDTO> create(
            @Valid @RequestBody ProductCategoryRecordDTO categoryDTO) {

        return ResponseEntity.ok(
                productCategoryService.create(categoryDTO)
        );
    }

    @PutMapping
    @Validated(OnUpdate.class)
    public ResponseEntity<ProductCategoryRecordDTO> update(
            @Valid @RequestBody ProductCategoryRecordDTO categoryDTO) {

        return ResponseEntity.ok(
                productCategoryService.update(categoryDTO)
        );
    }

    @GetMapping("/id/{categId}")
    public ResponseEntity<ProductCategoryRecordDTO> getById(
            @NotNull @PathVariable Integer categId) {

        return ResponseEntity.ok(
                productCategoryService.findById(categId)
        );
    }
}

