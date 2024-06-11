package com.delivery.app.security.controller;

import com.delivery.app.configs.validation.common.OnCreate;
import com.delivery.app.configs.validation.common.OnFilter;
import com.delivery.app.security.dtos.*;
import com.delivery.app.security.services.KeycloakUserService;
import com.delivery.app.security.services.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
            @Valid @RequestBody UserRegistrationRecordDTO userRegistrationRecordDTO) {

        UserRegistrationRecordDTO userCreate = keycloakUserService.createUser(userRegistrationRecordDTO);

        roleService.assignRole(userCreate.id(), userCreate.roleName());

        return keycloakUserService.getUserById(userCreate.id());
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

}
