package com.delivery.app.mobile.service;

import com.delivery.app.configs.DeliciasAppProperties;
import com.delivery.app.configs.exception.common.ResourceNotFoundException;
import com.delivery.app.mobile.dtos.MobileRestaurantDetailDTO;
import com.delivery.app.product.template.repositories.ProductTemplateRepository;
import com.delivery.app.restaurant.menu.model.RestaurantTmplMenu;
import com.delivery.app.restaurant.menu.repository.RestaurantTmplMenuRepository;
import com.delivery.app.restaurant.schedule.model.RestaurantTmplSchedule;
import com.delivery.app.restaurant.template.model.RestaurantTemplate;
import com.delivery.app.restaurant.template.repository.RestaurantTemplateRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@AllArgsConstructor
@Service
public class MobileRestaurantService {


    private final RestaurantTemplateRepository restaurantTemplateRepository;
    private final ProductTemplateRepository productTemplateRepository;
    private final RestaurantTmplMenuRepository restaurantTmplMenuRepository;
    private final DeliciasAppProperties deliciasAppProperties;

    public MobileRestaurantDetailDTO detail(Integer restaurantId) {

        RestaurantTemplate restaurantTmpl = restaurantTemplateRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("RestaurantTmpl", "id", restaurantId));

        DayOfWeek today = LocalDateTime.now().plusHours(deliciasAppProperties.getTimezone()).getDayOfWeek();
        LocalTime realTime = LocalTime.now().plusHours(deliciasAppProperties.getTimezone());

        RestaurantTmplSchedule todaySchedule = restaurantTmpl.getSchedules().stream().filter(r -> r.getDayOfWeek().equals(today)).findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Schedule", "today", today));

        List<ProductItem> allProducts = getAllProducts(restaurantTmpl.getMenus(), restaurantTmpl.getRecommendedProductsTmpl());

        return MobileRestaurantDetailDTO.builder()
                        .id(restaurantTmpl.getId())
                        .name(restaurantTmpl.getName())
                        .imageCover(Optional.ofNullable(restaurantTmpl.getImageCover())
                                .map(p-> String.format("%s/%s", deliciasAppProperties.getFiles().getResources(), p))
                                .orElse(deliciasAppProperties.getFiles().getStaticDefault()))
                        .info(MobileRestaurantDetailDTO.RestaurantInfo.builder()
                                .imageLogo(Optional.ofNullable(restaurantTmpl.getImageLogo())
                                        .map(p-> String.format("%s/%s", deliciasAppProperties.getFiles().getResources(), p))
                                        .orElse(deliciasAppProperties.getFiles().getStaticDefault()))
                                .hourStart(todaySchedule.getStartTime())
                                .hourEnd(todaySchedule.getEndTime())
                                .available(realTime.isAfter(todaySchedule.getStartTime()) && realTime.isBefore(todaySchedule.getEndTime()))
                                .address(Optional.ofNullable(restaurantTmpl.getAddress()).orElse(""))
                                .build()
                        )
                        .recommended(
                                allProducts.stream().filter(p -> restaurantTmpl.getRecommendedProductsTmpl().contains(p.id))
                                        .map(r -> new MobileRestaurantDetailDTO.RecommendedItem(r.id(), r.picture(), r.name(), r.listPrice(), r.categName()))
                                        .toList()
                        )
                        .menu(restaurantTmpl.getMenus().stream()
                                .map(menu -> new MobileRestaurantDetailDTO.Menu(
                                        menu.getId(),
                                        menu.getName(),
                                        allProducts.stream()
                                                .filter(product -> menu.getProductsTmpl().contains(product.id()))
                                                .map(a -> MobileRestaurantDetailDTO.ProductMenu.builder()
                                                        .id(a.id())
                                                        .name(a.name())
                                                        .picture(a.picture())
                                                        .priceList(a.listPrice())
                                                        .build()).toList())
                                )
                                .toList())
                        .build();
    }

    private List<ProductItem> getAllProducts(Set<RestaurantTmplMenu> menus, List<Integer> recommended) {

        List<Integer> menuIds = new ArrayList<>(menus
                .stream()
                .flatMap(listContainer -> listContainer.getProductsTmpl().stream())
                .toList());

        menuIds.addAll(Optional.ofNullable(recommended).orElse(List.of()));

        return productTemplateRepository.findByIdIn(menuIds).stream()
                .map(r -> new ProductItem(
                        r.getId(),
                        r.getName(),
                        Optional.ofNullable(r.getPicture())
                                .map(p-> String.format("%s/%s", deliciasAppProperties.getFiles().getResources(), p))
                                .orElse(deliciasAppProperties.getFiles().getStaticDefault()),
                        r.getListPrice(),
                        r.getCategory().getName()
                ))
                .toList();
    }


    private record ProductItem(
            Integer id,
            String name,
            String picture,
            double listPrice,
            String categName
    ){ }

}
