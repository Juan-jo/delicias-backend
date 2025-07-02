package com.delivery.app.mobile.app.service;

import com.delivery.app.configs.exception.common.ResourceNotFoundException;
import com.delivery.app.delicias.deliveryzone.model.DeliveryZone;
import com.delivery.app.delicias.deliveryzone.repository.DeliveryZoneRepository;
import com.delivery.app.mobile.app.dto.MobileUserRegisterDTO;
import com.delivery.app.mobile.exception.DeliveryZoneInvalidException;
import com.delivery.app.security.dtos.UserDTO;
import com.delivery.app.security.services.KeycloakUserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Service
public class MobileUserRegisterService {

    private final DeliveryZoneRepository deliveryZoneRepository;
    private final KeycloakUserService keycloakUserService;

    public Map<String, Object> register(MobileUserRegisterDTO req) {

        List<DeliveryZone> zones = deliveryZoneRepository.findZonesContainingPoint(req.latitude(), req.longitude());

        if(zones.isEmpty()){
            throw new DeliveryZoneInvalidException("");
        }

        DeliveryZone zone = zones.stream().findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("DeliveryZone", "first", ""));

        UserDTO user = keycloakUserService.createUser(UserDTO.builder()
                        .username(extractUsername(req.email()))
                        .email(req.email())
                        .firstName(req.name())
                        .lastName(req.lastName())
                        .password(req.pwd())
                        .deliveryZoneId(zone.getId())
                .build());

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("uuid", user.id());

        return response;
    }

    public static String extractUsername(String email) {
        // Validar que no sea null y tenga un solo "@"
        if (email == null || !email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            throw new IllegalArgumentException("Email inválido");
        }

        // Extraer la parte antes del @
        int atIndex = email.indexOf('@');
        return email.substring(0, atIndex);
    }
}
