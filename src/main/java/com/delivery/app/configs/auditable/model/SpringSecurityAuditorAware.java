package com.delivery.app.configs.auditable.model;

import com.delivery.app.security.services.KeycloakUserService;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class SpringSecurityAuditorAware implements AuditorAware<String> {

    @Autowired
    private KeycloakUserService keycloakUserService;

    @Override
    public Optional<String> getCurrentAuditor() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.of("jj");
        }

        UserRepresentation resp = keycloakUserService.getUserById(authentication.getName());

        return Optional.of(resp.getUsername());

    }
}
