package com.delivery.app.security.services;

import com.delivery.app.configs.exception.common.ResourceNotFoundException;
import com.delivery.app.security.dtos.*;
import com.delivery.app.security.exceptions.EmailAlreadyExistsException;
import com.delivery.app.security.exceptions.UsernameAlreadyExistsDTO;
import jakarta.ws.rs.core.Response;
import org.apache.commons.collections4.CollectionUtils;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.MappingsRepresentation;
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

    private static final String ROLE_PREFIX = "ROLE_";
    private static final String ATTR_STORE = "store";
    private static final String ROLE_STORE = "ROLE_STORE";

    @Value("${keycloak.realm}")
    private String realm;
    private final Keycloak keycloak;

    public KeycloakUserServiceImpl(Keycloak keycloak) {
        this.keycloak = keycloak;
    }
    @Override
    public UserDTO createUser(UserDTO userDTO) {

        UserRepresentation user = getUser(userDTO);

        UsersResource usersResource = getUsersResource();

        Response response = usersResource.create(user);

        if(Objects.equals(201,response.getStatus())){

            List<UserRepresentation> representationList = usersResource.searchByUsername(userDTO.username(), true);
            if(!CollectionUtils.isEmpty(representationList)){
                UserRepresentation userRepresentation1 = representationList.stream()
                        .filter(userRepresentation -> Objects.equals(false, userRepresentation.isEmailVerified()))
                        .findFirst().orElse(null);
                assert userRepresentation1 != null;

                emailVerification(userRepresentation1.getId());
                return UserDTO.builder()
                        .id(userRepresentation1.getId())
                        .roleName(userDTO.roleName())
                        .build();
            }

            return userDTO;
        }
        else {

            List<UserRepresentation> resultUsername = usersResource.searchByUsername(userDTO.username(), true);

            if(!resultUsername.isEmpty()) {
                throw new UsernameAlreadyExistsDTO(userDTO.username());
            }

            List<UserRepresentation> resultEmail = usersResource.searchByEmail(userDTO.email(), true);
            if(!resultEmail.isEmpty()) {
                throw  new EmailAlreadyExistsException(userDTO.email());
            }

        }

        return null;
    }

    @Override
    public UserDTO updateUser(UserDTO userDTO) {

        UserRepresentation userRepresentation = getUserById(userDTO.id());
        userRepresentation.setEmail(userDTO.email());
        userRepresentation.setFirstName(userDTO.firstName());
        userRepresentation.setLastName(userDTO.lastName());

        if(userDTO.roleName().equals(ROLE_STORE)) {

            Optional.ofNullable(userDTO.restaurantId()).ifPresentOrElse(storeId -> {

                userRepresentation.singleAttribute(ATTR_STORE, String.valueOf(userDTO.restaurantId()));

            }, () -> {
                throw new ResourceNotFoundException("isnotfound", "", "");
            });
        }
        else {
            userRepresentation.singleAttribute(ATTR_STORE, null);
        }

        getUsersResource().get(userDTO.id()).update(userRepresentation);

        return userDTO;
    }


    private static UserRepresentation getUser(UserDTO userDTO) {
        UserRepresentation user = new UserRepresentation();
        user.setEnabled(true);
        user.setUsername(userDTO.username());
        user.setEmail(userDTO.email());
        user.setFirstName(userDTO.firstName());
        user.setLastName(userDTO.lastName());
        user.setEmailVerified(false);

        if(userDTO.roleName().equals(ROLE_STORE)) {

            Optional.ofNullable(userDTO.restaurantId()).ifPresentOrElse(storeId -> {

                user.singleAttribute(ATTR_STORE, String.valueOf(userDTO.restaurantId()));

            }, () -> {
                throw new ResourceNotFoundException("isnotfound", "", "");
            });
        }

        CredentialRepresentation credentialRepresentation=new CredentialRepresentation();
        credentialRepresentation.setValue(userDTO.password());
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

        getUsersResource().get(userId).remove();
    }

    @Override
    public void emailVerification(String userId) {

    }

    @Override
    public UserResource getUserResource(String userId) {
        return getUsersResource().get(userId);
    }

    @Override
    public void updatePassword(UserChangePasswordDTO userChangePasswordDTO) {

        UserResource userResource = getUserResource(userChangePasswordDTO.id());

        CredentialRepresentation newCredential = new CredentialRepresentation();
        newCredential.setTemporary(false);
        newCredential.setType(CredentialRepresentation.PASSWORD);
        newCredential.setValue(userChangePasswordDTO.password());

        userResource.resetPassword(newCredential);
    }

    @Override
    public UserDTO findById(String id) {

        UserResource userResource = getUserResource(id);
        MappingsRepresentation roles = userResource.roles().getAll();
        UserRepresentation user =  userResource.toRepresentation();

        String userRole = roles.getRealmMappings()
                .stream()
                .map(RoleRepresentation::getName)
                .filter(name -> name.startsWith(ROLE_PREFIX))
                .collect(Collectors.joining());

        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .roleName(userRole)
                .restaurantId(Optional.ofNullable(user.getAttributes())
                        .map(r -> {

                            if(r.containsKey(ATTR_STORE)) {

                                String firstAttr = r.get(ATTR_STORE).get(0);

                                return Integer.valueOf(firstAttr);
                            }

                            return null;
                        })
                        .orElse(null))
                .build();
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
}
