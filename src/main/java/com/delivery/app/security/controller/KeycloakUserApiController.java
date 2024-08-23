package com.delivery.app.security.controller;

import com.delivery.app.configs.validation.common.OnCreate;
import com.delivery.app.configs.validation.common.OnFilter;
import com.delivery.app.configs.validation.common.OnUpdate;
import com.delivery.app.security.dtos.*;
import com.delivery.app.security.services.KeycloakUserService;
import com.delivery.app.security.services.RoleService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@CrossOrigin(originPatterns = {"*"})
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Validated
public class KeycloakUserApiController {

    private final KeycloakUserService keycloakUserService;
    private final RoleService roleService;

    @PostMapping
    @Validated(OnCreate.class)
    public UserRepresentation createUser(
            @Valid @RequestBody UserDTO userDTO) {

        UserDTO userCreate = keycloakUserService.createUser(userDTO);

        roleService.assignRole(userCreate.id(), userCreate.roleName());

        return keycloakUserService.getUserById(userCreate.id());
    }

    @PutMapping
    @Validated(OnUpdate.class)
    public UserRepresentation updateUser(
            @Valid @RequestBody UserDTO userDTO) {

        UserDTO userCreate = keycloakUserService.updateUser(userDTO);

        roleService.assignRole(userCreate.id(), userCreate.roleName());

        return keycloakUserService.getUserById(userCreate.id());
    }

    @PutMapping("/password")
    @Validated(OnUpdate.class)
    public ResponseEntity<?>  updatePassword(
            @Valid @RequestBody UserChangePasswordDTO changePasswordDTO) {

        keycloakUserService.updatePassword(changePasswordDTO);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{userId}")
    public UserDTO findById(@NotNull @PathVariable String userId) {

        return keycloakUserService.findById(userId);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteUser(@NotNull @PathVariable String userId) {
        keycloakUserService.deleteUserById(userId);
        return ResponseEntity.noContent().build();
    }


    @PostMapping("/search")
    @Validated(OnFilter.class)
    public Page<UserRowDTO> search(
            @Valid @RequestBody UserReqFilterRowsDTO reqFilterRowsDTO) {

        return keycloakUserService.search(
                reqFilterRowsDTO,
                roleService.getRoles());
    }

    @GetMapping("/roles")
    public List<RoleDTO> roles() {
        return roleService.getRoles();
    }


    @GetMapping("/address")
    public ResponseEntity<List<UserAddressDTO>> loadAddress(
            Principal principal
    ) {

        String keycloakUserId = principal.getName();

        return ResponseEntity.ok(
                keycloakUserService.loadAddress(UUID.fromString(keycloakUserId))
        );

    }
}
