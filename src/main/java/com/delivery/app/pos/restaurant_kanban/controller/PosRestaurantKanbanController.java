package com.delivery.app.pos.restaurant_kanban.controller;

import com.delivery.app.configs.validation.common.OnUpdate;
import com.delivery.app.pos.restaurant_kanban.dtos.PosRestaurantKanbanDTO;
import com.delivery.app.pos.restaurant_kanban.dtos.UpdatePosRestaurantKanbanDTO;
import com.delivery.app.pos.restaurant_kanban.service.PosRestaurantKanbanService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(originPatterns = {"*"})
@RestController
@RequestMapping("/api/pos/kanban")
@RequiredArgsConstructor
@Validated
public class PosRestaurantKanbanController {

    private final PosRestaurantKanbanService posRestaurantKanbanService;


    @GetMapping
    public ResponseEntity<PosRestaurantKanbanDTO> load() {

        return ResponseEntity.ok(
                posRestaurantKanbanService.loadKanban(7)
        );
    }

    @PutMapping
    @Validated(OnUpdate.class)
    public ResponseEntity<?> update(
            @Valid @RequestBody UpdatePosRestaurantKanbanDTO updatePosRestaurantKanbanDTO
            ) {

        posRestaurantKanbanService.updateKanbanItem(updatePosRestaurantKanbanDTO);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PosRestaurantKanbanDTO.Order> load(
            @NotNull @PathVariable Integer id
    ) {

        return ResponseEntity.ok(
                posRestaurantKanbanService.findKanban(id)
        );
    }

}
