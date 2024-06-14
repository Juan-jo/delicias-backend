package com.delivery.app.security.services;

import com.delivery.app.security.dtos.*;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.data.domain.Page;

import java.util.List;

public interface KeycloakUserService {
    UserDTO createUser(UserDTO userDTO);
    UserDTO updateUser(UserDTO userDTO);
    UserRepresentation getUserById(String userId);
    void deleteUserById(String userId);
    void emailVerification(String userId);
    UserResource getUserResource(String userId);
    void updatePassword(UserChangePasswordDTO userChangePasswordDTO);

    UserDTO findById(String id);
    List<UserRowDTO> searchList(UserReqFilterRowsDTO reqFilterRowsDTO);
    Page<UserRowDTO> search(UserReqFilterRowsDTO reqFilterRowsDTO, List<RoleDTO> roles);
}
