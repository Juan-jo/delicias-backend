package com.delivery.app.restaurant.template.controller;


import com.delivery.app.configs.validation.common.OnCreate;
import com.delivery.app.configs.validation.common.OnFilter;
import com.delivery.app.configs.validation.common.OnUpdate;
import com.delivery.app.restaurant.template.dto.RestaurantTemplateDTO;
import com.delivery.app.restaurant.template.dto.RestaurantTemplateReqFilterRows;
import com.delivery.app.restaurant.template.dto.RestaurantTemplateRow;
import com.delivery.app.restaurant.template.dto.RestaurantTmplOptionDTO;
import com.delivery.app.restaurant.template.service.RestaurantTemplateService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(originPatterns = {"*"})
@RestController
@RequestMapping("/api/restaurant/template")
@RequiredArgsConstructor
@Validated
public class RestaurantTemplateController {

    private final RestaurantTemplateService restaurantTemplateService;

    @PostMapping
    @Validated(OnCreate.class)
    public ResponseEntity<RestaurantTemplateDTO> create(
            @Valid @RequestBody RestaurantTemplateDTO templateDTO) {

        return ResponseEntity.ok(
                restaurantTemplateService.create(templateDTO)
        );
    }

    @PutMapping
    @Validated(OnUpdate.class)
    public ResponseEntity<RestaurantTemplateDTO> update(
            @Valid @RequestBody RestaurantTemplateDTO templateDTO) {

        return ResponseEntity.ok(
                restaurantTemplateService.update(templateDTO)
        );
    }

    @GetMapping("/{resTmplId}")
    public ResponseEntity<RestaurantTemplateDTO> getById(
            @NotNull @PathVariable Integer resTmplId
    ) {

        return ResponseEntity.ok(
                restaurantTemplateService.findById(resTmplId)
        );
    }

    @DeleteMapping("/{tmplId}")
    public ResponseEntity<?> deleteById(
            @NotNull @PathVariable Integer tmplId
    ) {

        restaurantTemplateService.deleteById(tmplId);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/filter")
    @Validated(OnFilter.class)
    public ResponseEntity<Page<RestaurantTemplateRow>> filter(
            @Valid @RequestBody RestaurantTemplateReqFilterRows reqFilterRows
            ) {

        return ResponseEntity.ok(
                restaurantTemplateService.findByFilter(reqFilterRows)
        );
    }


    @GetMapping("/options")
    public ResponseEntity<List<RestaurantTmplOptionDTO>> tmplList() {

        return ResponseEntity.ok(
                restaurantTemplateService.tmplOptionDTOList()
        );

    }

}
