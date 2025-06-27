package com.delivery.app.mobile.app.service;

import com.delivery.app.configs.DeliciasAppProperties;
import com.delivery.app.configs.exception.common.ResourceNotFoundException;
import com.delivery.app.mobile.app.dto.ChargesDTO;
import com.delivery.app.mobile.app.dto.MobileConfigDTO;
import com.delivery.app.mobile.app.model.MobileConfig;
import com.delivery.app.mobile.app.repository.MobileConfigRepository;
import com.delivery.app.restaurant.template.model.RestaurantTemplate;
import com.delivery.app.restaurant.template.repository.RestaurantTemplateRepository;
import com.delivery.app.security.model.UserAddress;
import com.delivery.app.security.repository.UserAddressRepository;
import com.delivery.app.utils.GoogleMapsService;
import com.google.maps.model.DistanceMatrix;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@AllArgsConstructor
@Service
public class MobileConfigService {

    private final MobileConfigRepository mobileConfigRepository;
    private final RestaurantTemplateRepository restaurantTemplateRepository;
    private final GoogleMapsService googleMapsService;
    private final DeliciasAppProperties deliciasAppProperties;
    private final UserAddressRepository userAddressRepository;

    private static final Integer confId = 27032024;

    public ChargesDTO loadCharges(Integer restaurantId, Double latitude,  Double longitude) {

        Long distance = getDistance(restaurantId, latitude, longitude);

        MobileConfigDTO configDTO = mobileConfigRepository.findById(confId)
                .map(modelToDTO())
                .orElseThrow(() -> new ResourceNotFoundException("Mobile Config", "id", confId));

        long _1km = 1000L; // 1 km = 1000 meters
        Long _2km = 2000L; // 2 km = 2000 meters

        Double costService = configDTO.costService();

        if(distance > _2km) {

            long km = _2km;

            while (km < distance) {

                costService += 5d;
                km += _1km;
            }
        }

        return new ChargesDTO(
                costService
        );
    }

    public MobileConfigDTO mobileConfig() {

        return mobileConfigRepository.findById(confId)
                .map(modelToDTO())
                .orElseThrow(() -> new ResourceNotFoundException("Mobile Config", "id", confId));
    }

    public ChargesDTO loadCharges(
            Integer restaurantId,
            Integer userAddressId
    ) {

        UserAddress address = userAddressRepository.findById(userAddressId)
                .orElseThrow(() -> new ResourceNotFoundException("UserAddress", "id", userAddressId));

        return loadCharges(restaurantId, address.getPosition().getY(), address.getPosition().getX());
    }

    private Function<MobileConfig, MobileConfigDTO> modelToDTO() {
        return model -> MobileConfigDTO.builder()
                .costService(model.getCostService())
                .availableRestaurants(Optional.ofNullable(model.getAvailableRestaurants()).orElse(List.of()))
                .latitude(model.getZone().getY())
                .longitude(model.getZone().getX())
                .build();
    }


    private Long getDistance(Integer restaurantId, Double latitude,  Double longitude) {
        Long distance = 0L;

        try {

            if(deliciasAppProperties.getProduction()) {

                RestaurantTemplate restaurantTemplate = restaurantTemplateRepository.findById(restaurantId).orElseThrow(
                        () -> new ResourceNotFoundException("restaurant", "id", restaurantId));

                Double origenLat = restaurantTemplate.getPosition().getY();
                Double origenLgn = restaurantTemplate.getPosition().getX();

                DistanceMatrix r = googleMapsService.calculateDistance(
                        new String[]{ String.format("%s %s", origenLat, origenLgn) },
                        new String[]{ String.format("%s %s", latitude, longitude)}
                );

                if(r.rows.length > 0 && r.rows[0].elements.length > 0) {

                    distance = r.rows[0].elements[0].distance.inMeters;
                }

                System.out.printf("*** GOOGLE *** fetch distance with matrix api: %s %n", distance);
            }
            else {
                distance = restaurantTemplateRepository.distanceMetersTo(restaurantId, longitude, latitude);
                System.out.printf("*** POSTGRES *** get distance: %s %n", distance);
            }
        }
        catch (Exception e) {

            distance = restaurantTemplateRepository.distanceMetersTo(restaurantId, longitude, latitude);
        }

        return distance; // return in meters
    }
}
