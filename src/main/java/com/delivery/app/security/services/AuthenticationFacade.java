package com.delivery.app.security.services;

import lombok.AllArgsConstructor;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.stream.Collectors;

@AllArgsConstructor
@Component
public class AuthenticationFacade {

    private static final String ROLE_STORE = "ROLE_STORE";
    private KeycloakUserService keycloakUserService;


    public String userRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserResource user = keycloakUserService.getUserResource(authentication.getName());

        return user.roles().realmLevel().listAll().stream()
                .map(RoleRepresentation::getName)
                .filter(name -> name.startsWith("ROLE_"))
                .collect(Collectors.joining());
    }

    public Integer storeUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserResource user = keycloakUserService.getUserResource(authentication.getName());

        if(userRole().equals(ROLE_STORE)) {
            if(user.toRepresentation().getAttributes().containsKey("store")) {
                return Integer.valueOf(user.toRepresentation().getAttributes().get("store").get(0));
            }
        }

        return null;
    }

    public UUID userId() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return UUID.fromString(authentication.getName());
    }
}
