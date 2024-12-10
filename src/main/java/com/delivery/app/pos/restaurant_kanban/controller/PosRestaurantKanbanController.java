package com.delivery.app.pos.restaurant_kanban.controller;

import com.delivery.app.configs.constants.RoleType;
import com.delivery.app.configs.validation.common.OnUpdate;
import com.delivery.app.pos.restaurant_kanban.dtos.PosRestaurantKanbanDTO;
import com.delivery.app.pos.restaurant_kanban.dtos.UpdatePosRestaurantKanbanDTO;
import com.delivery.app.pos.restaurant_kanban.service.PosRestaurantKanbanService;
import com.delivery.app.security.exceptions.UserRootException;
import com.delivery.app.security.services.AuthenticationFacade;
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
    private final AuthenticationFacade authenticationFacade;

    @GetMapping("/restaurant")
    public ResponseEntity<Integer> loadRestaurant() {

        if(authenticationFacade.userRole().equals(RoleType.ROOT.value())) {

            throw new UserRootException();
        }
        return ResponseEntity.ok(
                authenticationFacade.storeUser()
        );
    }

    @GetMapping("/load/{restaurantId}")
    public ResponseEntity<PosRestaurantKanbanDTO> loadKanban(
            @NotNull @PathVariable Integer restaurantId
    ) {

        return ResponseEntity.ok(
                posRestaurantKanbanService.loadKanban(restaurantId)
        );
    }

    @PutMapping
    @Validated(OnUpdate.class)
    public ResponseEntity<?> update(
            @Valid @RequestBody UpdatePosRestaurantKanbanDTO updatePosRestaurantKanbanDTO
            ) {

        posRestaurantKanbanService.updateStatusKanbanItem(updatePosRestaurantKanbanDTO);
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
