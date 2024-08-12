package com.delivery.app.mobile.service;

import com.delivery.app.configs.DeliciasAppProperties;
import com.delivery.app.configs.exception.common.ResourceNotFoundException;
import com.delivery.app.mobile.dtos.MobileRestaurantDetailDTO;
import com.delivery.app.product.template.repositories.ProductTemplateRepository;
import com.delivery.app.restaurant.menu.model.RestaurantTmplMenu;
import com.delivery.app.restaurant.menu.repository.RestaurantTmplMenuRepository;
import com.delivery.app.restaurant.template.repository.RestaurantTemplateRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class MobileRestaurantService {


    private final RestaurantTemplateRepository restaurantTemplateRepository;
    private final ProductTemplateRepository productTemplateRepository;
    private final RestaurantTmplMenuRepository restaurantTmplMenuRepository;
    private final DeliciasAppProperties deliciasAppProperties;

    public MobileRestaurantDetailDTO detail(Integer restaurantId) {

        List<RestaurantTmplMenu> menus = restaurantTmplMenuRepository.findByRestaurantTmplId(restaurantId);

        List<MobileRestaurantDetailDTO.ProductMenu> products = getProductMenus(menus);

        return restaurantTemplateRepository.findById(restaurantId)
                .map(r -> MobileRestaurantDetailDTO.builder()
                        .id(r.getId())
                        .name(r.getName())
                        .imageCover("https://picsum.photos/600")
                        .info(MobileRestaurantDetailDTO.RestaurantInfo.builder()
                                .imageLogo("https://picsum.photos/100")
                                .hourStart("07:00 am")
                                .hourEnd("18:00 pm")
                                .address("Calle paz #535")
                                .build())
                        .recommended(List.of(
                                new MobileRestaurantDetailDTO.RecommendedItem(1, "https://picsum.photos/200", "Product 1", 50.0, "Categ 1"),
                                new MobileRestaurantDetailDTO.RecommendedItem(2, "https://picsum.photos/200", "Product 2", 55.0, "Categ 2"),
                                new MobileRestaurantDetailDTO.RecommendedItem(3, "https://picsum.photos/200", "Product 3", 70.0, "Categ 3"),
                                new MobileRestaurantDetailDTO.RecommendedItem(4, "https://picsum.photos/200", "Product 4", 60.0, "Categ 4"),
                                new MobileRestaurantDetailDTO.RecommendedItem(5, "https://picsum.photos/200", "Product 5", 55.0, "Categ 5")
                        ))
                        .menu(menus.stream()
                                .map(menu -> new MobileRestaurantDetailDTO.Menu(
                                        menu.getId(),
                                        menu.getName(),
                                        products.stream().filter(product -> menu.getProductsTmpl().contains(product.id())).toList())
                                )
                                .toList())
                        .build())
                .orElseThrow(() -> new ResourceNotFoundException("RestaurantTmpl", "id", restaurantId));
    }

    private List<MobileRestaurantDetailDTO.ProductMenu> getProductMenus(List<RestaurantTmplMenu> menus) {
        List<Integer> allIds = menus
                .stream()
                .flatMap(listContainer -> listContainer.getProductsTmpl().stream())
                .toList();

        return productTemplateRepository.findByIdIn(
                allIds
        ).stream().map(r -> MobileRestaurantDetailDTO.ProductMenu.builder()
                        .id(r.getId())
                        .name(r.getName())
                        .picture(Optional.ofNullable(r.getPicture())
                                .map(p-> String.format("%s/%s", deliciasAppProperties.getFiles().getCoverSize(), p))
                                .orElse("https://picsum.photos/200"))
                        .priceList(r.getListPrice())
                        .build())
                .toList();
    }


}
