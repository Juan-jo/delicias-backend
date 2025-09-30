package com.delivery.app.mobile.app.service;

import com.delivery.app.configs.DeliciasAppProperties;
import com.delivery.app.configs.exception.common.ResourceNotFoundException;
import com.delivery.app.delicias.general.service.MobileConfigService;
import com.delivery.app.delicias.general.dto.MobileConfigDTO;
import com.delivery.app.mobile.app.dto.MobileRestaurantDetailDTO;
import com.delivery.app.mobile.app.dto.MobileRestaurantItemDTO;
import com.delivery.app.mobile.shopping.model.ShoppingCart;
import com.delivery.app.mobile.shopping.repository.ShoppingCartRepository;
import com.delivery.app.product.template.repositories.ProductTemplateRepository;
import com.delivery.app.restaurant.menu.model.RestaurantTmplMenu;
import com.delivery.app.restaurant.schedule.model.RestaurantTmplSchedule;
import com.delivery.app.restaurant.template.model.RestaurantTemplate;
import com.delivery.app.restaurant.template.repository.RestaurantTemplateRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@AllArgsConstructor
@Service
public class MobileRestaurantService {


    private final RestaurantTemplateRepository restaurantTemplateRepository;
    private final ProductTemplateRepository productTemplateRepository;
    private final DeliciasAppProperties deliciasAppProperties;
    private final ShoppingCartRepository shoppingCartRepository;
    private final MobileConfigService mobileConfigService;

    public MobileRestaurantDetailDTO detail(Integer restaurantId) {

        RestaurantTemplate restaurantTmpl = restaurantTemplateRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("RestaurantTmpl", "id", restaurantId));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID userUID = UUID.fromString(authentication.getName());

        ShoppingCart shoppingCart = shoppingCartRepository.findByUserUIDAndRestaurantId(
                userUID,
                restaurantId
        ).orElse(null);

        DayOfWeek today = LocalDateTime.now().plusHours(deliciasAppProperties.getTimezone()).getDayOfWeek();
        LocalTime realTime = LocalTime.now().plusHours(deliciasAppProperties.getTimezone());

        RestaurantTmplSchedule todaySchedule = restaurantTmpl.getSchedules().stream().filter(r -> r.getDayOfWeek().equals(today)).findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Schedule", "today", today));

        List<ProductItem> allProducts = getAllProducts(restaurantTmpl.getMenus(), restaurantTmpl.getRecommendedProductsTmpl());

        return MobileRestaurantDetailDTO.builder()
                        .id(restaurantTmpl.getId())
                        .name(restaurantTmpl.getName())
                        .alreadyExistsShoppingCart(shoppingCart != null)
                        .shoppingCartId(Optional.ofNullable(shoppingCart).map(ShoppingCart::getId).orElse(null))
                        .shoppingCartLinesSize(Optional.ofNullable(shoppingCart).map(i->i.getLines().size()).orElse(0))
                        .imageCover(Optional.ofNullable(restaurantTmpl.getImageCover())
                                .orElse(deliciasAppProperties.getSupabase().getLogo()))
                        .info(MobileRestaurantDetailDTO.RestaurantInfo.builder()
                                .imageLogo(Optional.ofNullable(restaurantTmpl.getImageLogo())
                                        .orElse(deliciasAppProperties.getSupabase().getLogo()))
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
                                        menu.getName(),
                                        allProducts.stream()
                                                .filter(product -> menu.getProductsTmpl().contains(product.id()))
                                                .map(a -> MobileRestaurantDetailDTO.ProductMenu.builder()
                                                        .id(a.id())
                                                        .name(a.name())
                                                        .picture(a.picture())
                                                        .priceList(a.listPrice())
                                                        .description(a.description())
                                                        .build()).toList())
                                )
                                .toList())
                        .build();
    }

    public List<MobileRestaurantItemDTO> loadRestaurants() {

        MobileConfigDTO mobileConfig = mobileConfigService.mobileConfig();

        return restaurantTemplateRepository.findByIdIn(mobileConfig.availableRestaurants()).stream().map( i ->
                new MobileRestaurantItemDTO(
                        i.getId(),
                        i.getName(),
                        i.getDescription(),
                        Optional.ofNullable(i.getImageLogo())
                                .orElse(deliciasAppProperties.getSupabase().getLogo()),
                        Optional.ofNullable(i.getImageCover())
                                .orElse(deliciasAppProperties.getSupabase().getLogo())
                ))
                .toList();
    }


    private List<ProductItem> getAllProducts(Set<RestaurantTmplMenu> menus, List<Integer> recommended) {

        List<Integer> menuIds = new ArrayList<>(menus
                .stream()
                .flatMap(listContainer -> listContainer.getProductsTmpl().stream())
                .toList());

        menuIds.addAll(Optional.ofNullable(recommended).orElse(List.of()));

        return productTemplateRepository.findByIdIn(menuIds).stream()
                .filter(f -> Optional.ofNullable(f.getListPrice()).orElse(0d) > 0)
                .map(r -> new ProductItem(
                        r.getId(),
                        r.getName(),
                        Optional.ofNullable(r.getPicture())
                                .orElse(deliciasAppProperties.getSupabase().getLogo()),
                        r.getListPrice(),
                        r.getCategory().getName(),
                        r.getDescription()
                ))
                .toList();
    }


    private record ProductItem(
            Integer id,
            String name,
            String picture,
            double listPrice,
            String categName,
            String description
    ){ }

}
