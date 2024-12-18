package com.delivery.app.mobile.app.controller;

import com.delivery.app.mobile.app.dto.MobileCategoryDTO;
import com.delivery.app.mobile.app.service.MobileCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin(originPatterns = {"*"})
@RestController
@RequestMapping("/api/mobile/category")
@RequiredArgsConstructor
@Validated
public class MobileCategoryController {

    private final MobileCategoryService mobileCategoryService;


    @GetMapping
    public ResponseEntity<List<MobileCategoryDTO>> load() {
        return ResponseEntity.ok(
                mobileCategoryService.loadAvailable()
        );
    }
}
