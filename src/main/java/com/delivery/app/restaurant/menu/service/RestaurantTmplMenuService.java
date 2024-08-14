package com.delivery.app.restaurant.menu.service;

import com.delivery.app.restaurant.menu.repository.RestaurantTmplMenuRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
@Service
public class RestaurantTmplMenuService {

    private final RestaurantTmplMenuRepository restaurantTmplMenuRepository;

    @Transactional
    public void deleteByMenuId(Integer menuId) {

        restaurantTmplMenuRepository.deleteById(menuId);
    }
}
