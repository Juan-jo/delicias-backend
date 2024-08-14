package com.delivery.app.restaurant.template.controller;

import com.delivery.app.configs.constants.RoleType;
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
import java.util.Map;

@CrossOrigin(originPatterns = {"*"})
@RestController
@RequestMapping("/api/restaurant/user")
@RequiredArgsConstructor
@Validated
public class UserRestaurantController {

    private final AuthenticationFacade authenticationFacade;


    @GetMapping
    public ResponseEntity<Map<String, Integer>> loadRestaurant() {

        if(authenticationFacade.userRole().equals(RoleType.ROOT.value())) {
            throw new UserRootException();
        }
        return ResponseEntity.ok(
            Collections.singletonMap("restaurantId", authenticationFacade.storeUser())
        );
    }
}
