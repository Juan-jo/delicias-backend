package com.delivery.app.product.template.controller;

import com.delivery.app.configs.validation.common.OnCreate;
import com.delivery.app.configs.validation.common.OnFilter;
import com.delivery.app.configs.validation.common.OnUpdate;
import com.delivery.app.product.template.dtos.*;
import com.delivery.app.product.template.services.ProductTemplateConfigService;
import com.delivery.app.product.template.services.ProductTemplateService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(originPatterns = {"*"})
@RestController
@RequestMapping("/api/product/template")
@RequiredArgsConstructor
@Validated
public class ProductTemplateController {

    private final ProductTemplateService productTemplateService;
    private final ProductTemplateConfigService productTemplateConfigService;

    @GetMapping("/{tmplId}")
    public ResponseEntity<ProductTemplateDTO> getById(
            @NotNull @PathVariable Integer tmplId) {

        return ResponseEntity.ok(
                productTemplateService.findById(tmplId)
        );
    }

    @PostMapping
    @Validated(OnCreate.class)
    public ResponseEntity<ProductTemplateRecordDTO> create(
            @Valid @RequestBody ProductTemplateRecordDTO recordDTO
            ) {

        return  ResponseEntity.ok(
                productTemplateService.create(recordDTO)
        );
    }

    @PutMapping
    @Validated(OnUpdate.class)
    public ResponseEntity<ProductTemplateRecordDTO> update(
            @Valid @RequestBody ProductTemplateRecordDTO recordDTO
    ) {

        return  ResponseEntity.ok(
                productTemplateService.update(recordDTO)
        );
    }

    @GetMapping("/{tmplId}/attributes")
    public List<ProductTmplAttributeRowDTO> getProductTmplAttributes(
            @NotNull @PathVariable Integer tmplId
    ) {
       return productTemplateService.attributeValuesRowDTOS(tmplId);
    }

    @GetMapping("/{tmplId}/config")
    public ResponseEntity<ProductTemplateConfigDTO> findConfig(
            @NotNull @PathVariable Integer tmplId
    ) {
        return ResponseEntity.ok(
                productTemplateConfigService.findConfigByProductTmplId(tmplId)
        );
    }

    @PutMapping("/config")
    @Validated(OnUpdate.class)
    public ResponseEntity<ProductTemplateConfigDTO> updateConfig(
            @Valid @RequestBody ProductTemplateConfigDTO templateConfigDTO
    ) {
        return ResponseEntity.ok(
                productTemplateConfigService.updateConfig(templateConfigDTO)
        );
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ROOT','ROLE_STORE')")
    @PostMapping("/filter")
    @Validated(OnFilter.class)
    public Page<ProductTemplateRowDTO> filter(
            @Valid @RequestBody ProductTemplateReqFilterRowsDTO reqFilterRowsDTO ) {

        return productTemplateService.searchFilter(reqFilterRowsDTO);
    }

}
