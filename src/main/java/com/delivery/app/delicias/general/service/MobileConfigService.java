package com.delivery.app.delicias.general.service;

import com.delivery.app.configs.DeliciasAppProperties;
import com.delivery.app.configs.exception.common.ResourceNotFoundException;
import com.delivery.app.delicias.general.dto.MobileConfigDTO;
import com.delivery.app.mobile.app.model.MobileConfig;
import com.delivery.app.mobile.app.repository.MobileConfigRepository;
import com.delivery.app.restaurant.template.dto.RestaurantTmplEnabledMobileDTO;
import com.delivery.app.restaurant.template.model.RestaurantTemplate;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@AllArgsConstructor
@Service
public class MobileConfigService {

    private final MobileConfigRepository mobileConfigRepository;
    private final DeliciasAppProperties deliciasAppProperties;

    public MobileConfigDTO mobileConfig() {

        return mobileConfigRepository.findById(deliciasAppProperties.getMobileConfigId())
                .map(modelToDTO())
                .orElseThrow(() -> new ResourceNotFoundException("Mobile Config", "id", deliciasAppProperties.getMobileConfigId()));
    }

    @Transactional
    public void update(MobileConfigDTO configDTO) {

        MobileConfig config = mobileConfigRepository.findById(deliciasAppProperties.getMobileConfigId())
                .orElseThrow(() -> new ResourceNotFoundException("Mobile Config", "id", deliciasAppProperties.getMobileConfigId()));

        config.update(configDTO);
    }

    // Enabled or disabled
    @Transactional
    public void enabledMobile(RestaurantTmplEnabledMobileDTO enabledMobileDTO) {

        MobileConfig config = mobileConfigRepository.findById(deliciasAppProperties.getMobileConfigId())
                .orElseThrow(() -> new ResourceNotFoundException("Mobile Config", "id", deliciasAppProperties.getMobileConfigId()));

        if(enabledMobileDTO.enabledMobile()) {
            config.enabledRestaurant(enabledMobileDTO.id());
        }
        else {
            config.disabledRestaurant(enabledMobileDTO.id());
        }

    }

    private Function<MobileConfig, MobileConfigDTO> modelToDTO() {
        return model -> MobileConfigDTO.builder()
                .availableRestaurants(Optional.ofNullable(model.getAvailableRestaurants()).orElse(List.of()))
                .minimumShippingCost(model.getMinimumShippingCost())
                .minimumShippingDistance(model.getMinimumShippingDistance())
                .shippingCostPerKm(model.getShippingCostPerKm())
                .hasMessageShippingCost(model.isHasMessageShippingCost())
                .messageShippingCost(model.getMessageShippingCost())
                .build();
    }

}
