package com.delivery.app.security;

import lombok.AllArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class KeycloakUserServiceImpl implements KeycloakUserService {

    @Value("${keycloak.realm}")
    private String realm;
    private final Keycloak keycloak;

    public KeycloakUserServiceImpl(Keycloak keycloak) {
        this.keycloak = keycloak;
    }
    @Override
    public UserRegistrationRecord createUser(UserRegistrationRecord userRegistrationRecord) {
        return null;

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
        return null;
    }

    @Override
    public void updatePassword(String userId) {

    }

    private UsersResource getUsersResource() {
        RealmResource realm1 = keycloak.realm(realm);
        return realm1.users();
    }
}
