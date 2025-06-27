package com.delivery.app.restaurant.template.controller;

import com.delivery.app.configs.DeliciasAppProperties;
import com.delivery.app.configs.constants.RoleType;
import com.delivery.app.restaurant.template.dto.RestaurantTemplateDTO;
import com.delivery.app.restaurant.template.service.RestaurantTemplateService;
import com.delivery.app.security.exceptions.UserRootException;
import com.delivery.app.security.services.AuthenticationFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(originPatterns = {"*"})
@RestController
@RequestMapping("/api/restaurant/user")
@RequiredArgsConstructor
@Validated
public class UserRestaurantController {

    private final AuthenticationFacade authenticationFacade;
    private final RestaurantTemplateService restaurantTemplateService;
    private final DeliciasAppProperties deliciasAppProperties;

    @GetMapping
    public ResponseEntity<Map<String, Object>> loadRestaurant() {

        if(authenticationFacade.userRole().equals(RoleType.ROOT.value())) {
            throw new UserRootException();
        }

        Map<String, Object> data = new HashMap<>();
        data.put("restaurantId", authenticationFacade.storeUser());

        Optional.ofNullable(authenticationFacade.storeUser()).ifPresent((val -> {


            RestaurantTemplateDTO res = restaurantTemplateService.findById(val);

            data.put("storeName", res.name());

            data.put("picture", (
                    Optional.ofNullable(res.logoPicture())
                            .map(c->String.format("%s/%s", deliciasAppProperties.getFiles().getResources(), c))
                            .orElse(null)
            ));

        }));



        return ResponseEntity.ok(
                data
        );
    }
}
