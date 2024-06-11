package com.delivery.app.security.services;

import com.delivery.app.security.dtos.RoleDTO;
import com.delivery.app.security.dtos.UserRegistrationRecordDTO;
import com.delivery.app.security.dtos.UserReqFilterRowsDTO;
import com.delivery.app.security.dtos.UserRowDTO;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.data.domain.Page;

import java.util.List;

public interface KeycloakUserService {
    UserRegistrationRecordDTO createUser(UserRegistrationRecordDTO userRegistrationRecordDTO);
    UserRepresentation getUserById(String userId);
    void deleteUserById(String userId);
    void emailVerification(String userId);
    UserResource getUserResource(String userId);
    void updatePassword(String userId);

    List<UserRowDTO> searchList(UserReqFilterRowsDTO reqFilterRowsDTO);
    Page<UserRowDTO> search(UserReqFilterRowsDTO reqFilterRowsDTO, List<RoleDTO> roles);
}
