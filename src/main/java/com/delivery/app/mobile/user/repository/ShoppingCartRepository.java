package com.delivery.app.mobile.user.repository;

import com.delivery.app.mobile.user.models.ShoppingCart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, UUID> {

    Optional<ShoppingCart> findByUserUIDAndRestaurantId(UUID userId, Integer restaurantId);

    List<ShoppingCart> findByUserUID(UUID userId);
}
