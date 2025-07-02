package com.delivery.app.delicias.deliveryzone.controller;

import com.delivery.app.configs.validation.common.OnCreate;
import com.delivery.app.configs.validation.common.OnFilter;
import com.delivery.app.configs.validation.common.OnUpdate;
import com.delivery.app.delicias.deliveryzone.dto.DeliverZoneLatLngDTO;
import com.delivery.app.delicias.deliveryzone.dto.DeliveryZoneDTO;
import com.delivery.app.delicias.deliveryzone.dto.DeliveryZoneReqFilter;
import com.delivery.app.delicias.deliveryzone.service.DeliveryZoneService;
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
@RequestMapping("/api/deliveryZones")
@RequiredArgsConstructor
@Validated
public class DeliveryZoneController {

    private final DeliveryZoneService deliveryZoneService;

    @PreAuthorize("hasAnyAuthority('ROLE_ROOT')")
    @PostMapping("/filter")
    @Validated(OnFilter.class)
    public ResponseEntity<Page<DeliveryZoneDTO>> filter(
            @Valid @RequestBody DeliveryZoneReqFilter reqFilter) {

        return ResponseEntity.ok(
                deliveryZoneService.filter(reqFilter)
        );
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ROOT')")
    @PostMapping
    @Validated(OnCreate.class)
    public ResponseEntity<DeliveryZoneDTO> create(
            @Valid @RequestBody DeliveryZoneDTO req) {

        return ResponseEntity.ok(
                deliveryZoneService.create(req)
        );
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ROOT')")
    @PutMapping
    @Validated(OnUpdate.class)
    public ResponseEntity<DeliveryZoneDTO> update(
            @Valid @RequestBody DeliveryZoneDTO req) {

        return ResponseEntity.ok(
                deliveryZoneService.update(req)
        );
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ROOT')")
    @GetMapping("/{deliveryZoneId}")
    public ResponseEntity<DeliveryZoneDTO> getById(
            @NotNull @PathVariable Integer deliveryZoneId) {

        return ResponseEntity.ok(
                deliveryZoneService.findById(deliveryZoneId)
        );
    }


    @PreAuthorize("hasAnyAuthority('ROLE_ROOT')")
    @DeleteMapping("/{deliveryZoneId}")
    public ResponseEntity<Void> deleteById(
            @NotNull @PathVariable Integer deliveryZoneId
    ) {

        deliveryZoneService.deleteById(deliveryZoneId);

        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ROOT')")
    @GetMapping("/all")
    public ResponseEntity<List<DeliverZoneLatLngDTO>> all() {

        return ResponseEntity.ok(
                deliveryZoneService.all()
        );
    }

}
