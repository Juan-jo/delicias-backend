package com.delivery.app.security.services;


import com.delivery.app.security.dtos.RoleDTO;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements  RoleService {

    @Value("${keycloak.realm}")
    private String realm;

    private final Keycloak keycloak;

    private final KeycloakUserService keycloakUserService;

    @Override
    public void assignRole(String userId, String roleName) {
        try {
            UserResource userResource = keycloakUserService.getUserResource(userId);
            if (userResource == null) {
                throw new IllegalArgumentException("User not found with ID: " + userId);
            }

            RolesResource rolesResource = getRolesResource();
            RoleRepresentation roleRepresentation = rolesResource.get(roleName).toRepresentation();

            if (roleRepresentation == null) {
                throw new IllegalArgumentException("Role representation not found for role: " + roleName);
            }

            userResource.roles().realmLevel().add(Collections.singletonList(roleRepresentation));
            System.out.println("Role " + roleName + " assigned to user " + userId + " successfully.");
        } catch (Exception e) {
            System.err.println("Error assigning role: " + e.getMessage());
        }
    }

    @Override
    public List<RoleDTO> getRoles() {
        return keycloak.realm(realm).roles().list().stream()
                .filter(s-> s.getName().startsWith("ROLE_"))
                .map(r -> RoleDTO.builder()
                        .roleName(r.getName())
                        .description(r.getDescription())
                        .build())
                .toList();
    }

    private RolesResource getRolesResource(){
        return  keycloak.realm(realm).roles();
    }
}
