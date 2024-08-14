package com.delivery.app.restaurant.menu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.delivery.app.restaurant.menu.model.RestaurantTmplMenu;

import java.util.List;

@Repository
public interface RestaurantTmplMenuRepository extends JpaRepository<RestaurantTmplMenu, Integer> {


    @Query("SELECT m FROM RestaurantTmplMenu m  WHERE m.restaurantTmpl.id = :restaurantId ORDER BY m.sequence ASC")
    List<RestaurantTmplMenu> findByRestaurantTmplId(Integer restaurantId);

}
