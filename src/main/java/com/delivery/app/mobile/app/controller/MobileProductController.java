package com.delivery.app.mobile.app.controller;

import com.delivery.app.mobile.app.dto.MobileProductRecommendedDTO;
import com.delivery.app.mobile.app.dto.MobileProductTmplDetailDTO;
import com.delivery.app.mobile.app.service.MobileProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(originPatterns = {"*"})
@RestController
@RequestMapping("/api/mobile/product")
@RequiredArgsConstructor
@Validated
public class MobileProductController {

    private final MobileProductService mobileProductService;



    @GetMapping("/{tmplId}")
    public ResponseEntity<MobileProductTmplDetailDTO> findById(
            @Valid @PathVariable Integer tmplId
    ) {
        return ResponseEntity.ok(
                mobileProductService.detail(tmplId)
        );
    }

    @GetMapping("/recommended")
    public ResponseEntity<List<MobileProductRecommendedDTO>> loadRecommended() {

        return ResponseEntity.ok(
                mobileProductService.loadRecommended()
        );
    }
}
