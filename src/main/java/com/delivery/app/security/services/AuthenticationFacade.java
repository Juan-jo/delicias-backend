package com.delivery.app.security.services;

import lombok.AllArgsConstructor;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.stream.Collectors;

import static com.delivery.app.security.services.KeycloakUserServiceImpl.ATTR_LAST_USER_ADDRESS_ID;

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

    public Integer lastUserAddressId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserResource userResource = keycloakUserService.getUserResource(authentication.getName());
        UserRepresentation user =  userResource.toRepresentation();

        var attrs = user.getAttributes();

        if(attrs != null) {

            if(attrs.containsKey(ATTR_LAST_USER_ADDRESS_ID)) {
                String firstAttr = attrs.get(ATTR_LAST_USER_ADDRESS_ID).get(0);

                return Integer.valueOf(firstAttr);
            }

        }

        return null;
    }

    public UUID userId() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return UUID.fromString(authentication.getName());
    }
}
