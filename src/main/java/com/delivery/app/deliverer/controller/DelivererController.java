package com.delivery.app.deliverer.controller;

import com.delivery.app.configs.validation.common.OnUpdate;
import com.delivery.app.deliverer.dto.DeliverUpdateLastLocationDTO;
import com.delivery.app.deliverer.service.DeliverService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(originPatterns = {"*"})
@RestController
@RequestMapping("/api/deliverer")
@RequiredArgsConstructor
@Validated
public class DelivererController {


    private final DeliverService deliverService;

    @PutMapping
    @Validated(OnUpdate.class)
    public ResponseEntity<?> update(
            @Valid @RequestBody DeliverUpdateLastLocationDTO lastLocationDTO) {

        deliverService.updateLastLocation(lastLocationDTO);
        return ResponseEntity.noContent().build();
    }
}
