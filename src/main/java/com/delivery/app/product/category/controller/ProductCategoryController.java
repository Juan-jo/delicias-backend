package com.delivery.app.product.category.controller;

import com.delivery.app.configs.validation.common.OnCreate;
import com.delivery.app.configs.validation.common.OnFilter;
import com.delivery.app.configs.validation.common.OnUpdate;
import com.delivery.app.product.category.dto.ProductCategoryParentOptionDTO;
import com.delivery.app.product.category.dto.ProductCategoryRecordDTO;
import com.delivery.app.product.category.dto.ProductCategoryReqFilterRows;
import com.delivery.app.product.category.dto.ProductCategoryRow;
import com.delivery.app.product.category.service.ProductCategoryService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

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

    @GetMapping("/{categId}")
    public ResponseEntity<ProductCategoryRecordDTO> getById(
            @NotNull @PathVariable Integer categId) {

        return ResponseEntity.ok(
                productCategoryService.findById(categId)
        );
    }

    @PostMapping("/filter")
    @Validated(OnFilter.class)
    public ResponseEntity<Page<ProductCategoryRow>> filter(
            @Valid @RequestBody ProductCategoryReqFilterRows reqFilterRows) {

        return ResponseEntity.ok(
                productCategoryService.findByFilter(reqFilterRows)
        );
    }

    @DeleteMapping("/{categId}")
    public ResponseEntity<Void> deleteById(
            @NotNull @PathVariable Integer categId
    ) {

        productCategoryService.deleteById(categId);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/parents")
    public ResponseEntity<List<ProductCategoryParentOptionDTO>> getParents() {

        return ResponseEntity.ok(
                productCategoryService.getParents()
        );
    }
}

