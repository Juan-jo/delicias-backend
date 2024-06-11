package com.delivery.app.security.services;

import com.delivery.app.configs.exception.common.ResourceNotFoundException;
import com.delivery.app.security.dtos.RoleDTO;
import com.delivery.app.security.dtos.UserRegistrationRecordDTO;
import com.delivery.app.security.dtos.UserReqFilterRowsDTO;
import com.delivery.app.security.dtos.UserRowDTO;
import jakarta.ws.rs.core.Response;
import org.apache.commons.collections4.CollectionUtils;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class KeycloakUserServiceImpl implements KeycloakUserService {

    @Value("${keycloak.realm}")
    private String realm;
    private final Keycloak keycloak;

    public KeycloakUserServiceImpl(Keycloak keycloak) {
        this.keycloak = keycloak;
    }
    @Override
    public UserRegistrationRecordDTO createUser(UserRegistrationRecordDTO userRegistrationRecordDTO) {

        UserRepresentation user = getUser(userRegistrationRecordDTO);

        UsersResource usersResource = getUsersResource();

        Response response = usersResource.create(user);

        if(Objects.equals(201,response.getStatus())){

            List<UserRepresentation> representationList = usersResource.searchByUsername(userRegistrationRecordDTO.username(), true);
            if(!CollectionUtils.isEmpty(representationList)){
                UserRepresentation userRepresentation1 = representationList.stream()
                        .filter(userRepresentation -> Objects.equals(false, userRepresentation.isEmailVerified()))
                        .findFirst().orElse(null);
                assert userRepresentation1 != null;

                emailVerification(userRepresentation1.getId());
                return UserRegistrationRecordDTO.builder()
                        .id(userRepresentation1.getId())
                        .roleName(userRegistrationRecordDTO.roleName())
                        .build();
            }

            return userRegistrationRecordDTO;
        }

        return null;
    }

    private static UserRepresentation getUser(UserRegistrationRecordDTO userRegistrationRecordDTO) {
        UserRepresentation user=new UserRepresentation();
        user.setEnabled(true);
        user.setUsername(userRegistrationRecordDTO.username());
        user.setEmail(userRegistrationRecordDTO.email());
        user.setFirstName(userRegistrationRecordDTO.firstName());
        user.setLastName(userRegistrationRecordDTO.lastName());
        user.setEmailVerified(false);

        if(userRegistrationRecordDTO.roleName().equals("ROLE_STORE")) {

            Optional.ofNullable(userRegistrationRecordDTO.restaurantId()).ifPresentOrElse(storeId -> {

                user.singleAttribute("store", String.valueOf(userRegistrationRecordDTO.restaurantId()));

            }, () -> {
                throw new ResourceNotFoundException("isnotfound", "", "");
            });
        }

        CredentialRepresentation credentialRepresentation=new CredentialRepresentation();
        credentialRepresentation.setValue(userRegistrationRecordDTO.password());
        credentialRepresentation.setTemporary(false);
        credentialRepresentation.setType(CredentialRepresentation.PASSWORD);

        List<CredentialRepresentation> list = new ArrayList<>();
        list.add(credentialRepresentation);
        user.setCredentials(list);
        return user;
    }

    @Override
    public UserRepresentation getUserById(String userId) {

        return  getUsersResource().get(userId).toRepresentation();
    }

    @Override
    public void deleteUserById(String userId) {


    }

    @Override
    public void emailVerification(String userId) {

    }

    @Override
    public UserResource getUserResource(String userId) {
        return getUsersResource().get(userId);
    }

    @Override
    public void updatePassword(String userId) {

    }

    @Override
    public List<UserRowDTO> searchList(UserReqFilterRowsDTO reqFilterRowsDTO) {

        List<UserRepresentation> users = getUsersResource().list(1, 10);

        return users.stream()
                .filter(u -> u.getAttributes().containsKey(reqFilterRowsDTO.roleName()))
                .map(r -> UserRowDTO.builder()
                        .id(r.getId())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public Page<UserRowDTO> search(UserReqFilterRowsDTO reqFilterRowsDTO,
                                   List<RoleDTO> roles) {

        final Page<UserRowDTO> users;

        List<String> finalRoles = roles
                .stream()
                .map(RoleDTO::roleName)
                .toList();

        Pageable pageable = PageRequest.of(reqFilterRowsDTO.page(), reqFilterRowsDTO.size());

        List<UserRowDTO> listofTicktes = getUsersResource()
                .search(reqFilterRowsDTO.userSearch())
                .stream()
                .filter(u -> {
                    boolean flag = false;

                    UserResource ur = getUserResource(u.getId());

                    List<String> uroles = ur.roles().realmLevel().listAll()
                            .stream()
                            .map(RoleRepresentation::getName)
                            .toList();

                    if(reqFilterRowsDTO.roleName() != null &&
                            !reqFilterRowsDTO.roleName().isEmpty()) {

                        return uroles.contains(reqFilterRowsDTO.roleName());
                    }
                    else {

                        for (String urr : uroles) {
                            flag = finalRoles.contains(urr);
                            if (flag) {
                                break;
                            }
                        }
                    }

                    return flag;
                })
                .map(r-> UserRowDTO.builder()
                        .id(r.getId())
                        .username(r.getUsername())
                        .email(r.getEmail())
                        .firstName(r.getFirstName())
                        .lastName(r.getLastName())
                        .build())
                .collect(Collectors.toList());

        final int start = (int) pageable.getOffset();
        final int end = Math.min((start + pageable.getPageSize()), listofTicktes.size());

        if (CollectionUtils.isNotEmpty(listofTicktes)) {
            users = new PageImpl<>(listofTicktes.subList(start, end), pageable, listofTicktes.size());
        } else {
            users = new PageImpl<>(listofTicktes, pageable, 0);
        }
        return users;

    }

    private UsersResource getUsersResource() {
        RealmResource realm1 = keycloak.realm(realm);
        return realm1.users();
    }

    private List<UserRepresentation> filter(String search) {

        RealmResource realm1 = keycloak.realm(realm);
        return realm1.users().search(search);

    }
}
