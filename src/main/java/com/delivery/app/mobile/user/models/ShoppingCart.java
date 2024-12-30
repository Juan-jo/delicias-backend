package com.delivery.app.mobile.user.models;

import com.delivery.app.restaurant.template.model.RestaurantTemplate;
import com.delivery.app.security.model.UserAddress;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "shopping_cart")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShoppingCart {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "user_uid")
    private UUID userUID;

    @ManyToOne
    @JoinColumn(name = "restaurant_id", referencedColumnName = "id")
    private RestaurantTemplate restaurant;

    @ManyToOne
    @JoinColumn(name = "user_address_id", referencedColumnName = "id")
    private UserAddress userAddress;
}
