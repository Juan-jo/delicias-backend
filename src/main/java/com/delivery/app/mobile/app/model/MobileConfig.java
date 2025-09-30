package com.delivery.app.mobile.app.model;

import com.delivery.app.delicias.general.dto.MobileConfigDTO;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "mobile_config")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MobileConfig {

    @Id
    private Integer id;

    @Column(columnDefinition = "int[]", name = "available_restaurants")
    private List<Integer> availableRestaurants;

    @Column(name = "minimum_shipping_cost")
    private Double minimumShippingCost;

    @Column(name = "minimum_shipping_distance")
    private Double minimumShippingDistance;

    @Column(name = "shipping_cost_per_km")
    private Double shippingCostPerKm;

    @Column(name = "has_message_shipping_cost")
    private boolean hasMessageShippingCost;

    @Column(name = "message_shipping_cost")
    private String messageShippingCost;

    public void update(MobileConfigDTO configDTO) {

        availableRestaurants = configDTO.availableRestaurants();
        minimumShippingCost = configDTO.minimumShippingCost();
        minimumShippingDistance = configDTO.minimumShippingDistance();
        shippingCostPerKm = configDTO.shippingCostPerKm();
        hasMessageShippingCost = configDTO.hasMessageShippingCost();
        messageShippingCost = configDTO.messageShippingCost();

    }

    public void enabledRestaurant(Integer restaurantId) {

        if(this.availableRestaurants == null) {
            this.availableRestaurants = new ArrayList<>();
        }

        this.availableRestaurants.add(restaurantId);
    }

    public void disabledRestaurant(Integer restaurantId) {

        this.availableRestaurants.remove(restaurantId);
    }

}
