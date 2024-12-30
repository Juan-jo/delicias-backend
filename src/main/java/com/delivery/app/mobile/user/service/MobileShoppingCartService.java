package com.delivery.app.mobile.user.service;

import com.delivery.app.configs.DeliciasAppProperties;
import com.delivery.app.configs.exception.common.ResourceNotFoundException;
import com.delivery.app.mobile.user.dtos.MobileShoppingCartAvailableDTO;
import com.delivery.app.mobile.user.dtos.MobileShoppingCartDTO;
import com.delivery.app.mobile.user.models.ShoppingCart;
import com.delivery.app.mobile.user.models.ShoppingCartLine;
import com.delivery.app.mobile.user.repository.ShoppingCartRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
@Service
public class MobileShoppingCartService {

    private final ShoppingCartRepository shoppingCartRepository;
    private final DeliciasAppProperties deliciasAppProperties;

    public List<MobileShoppingCartAvailableDTO> availableShoppingCart() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID userUid = UUID.fromString(authentication.getName());

        return shoppingCartRepository.findByUserUID(userUid)
                .stream().filter(item -> !item.getLines().isEmpty())
                .map(cart -> MobileShoppingCartAvailableDTO.builder()
                        .id(cart.getId())
                        .restaurantName(cart.getRestaurant().getName())
                        .restaurantLogo(Optional.ofNullable(cart.getRestaurant().getImageLogo())
                                .map(d -> String.format("%s/%s",deliciasAppProperties.getFiles().getResources(), d))
                                .orElse(deliciasAppProperties.getFiles().getStaticDefault()))
                        .lineCount(cart.getLines().size())
                        .build())
                .toList();
    }
    
    @Transactional(readOnly = true)
    public MobileShoppingCartDTO findById(UUID shoppingCartId) {

        ShoppingCart shoppingCart = shoppingCartRepository.findById(shoppingCartId)
                .orElseThrow(() -> new ResourceNotFoundException("ShoppingCart", "id", shoppingCartId));

        double subtotal = 0.0;
        double shipmentCost = 0.0;

        List<MobileShoppingCartDTO.ShoppingLine> lines = new ArrayList<>();

        for(ShoppingCartLine line: shoppingCart.getLines()) {

            // calculate total attr values
            double totalAmountAttrValues =  0.0;

            totalAmountAttrValues += line.getProductTemplate().getAttributeValues()
                    .stream()
                    .filter(attrVal -> line.getAttrValuesIds().contains(attrVal.getId()))
                    .map(i-> Optional.ofNullable(i.getExtraPrice()).orElse(0.0))
                    .reduce(0.0, Double::sum) * line.getQty();

            subtotal += totalAmountAttrValues + (line.getQty() * line.getProductTemplate().getListPrice());

            lines.add(MobileShoppingCartDTO.ShoppingLine.builder()
                            .id(line.getId())
                            .price(line.getProductTemplate().getListPrice())
                            .qty(line.getQty())
                            .productTmplName(line.getProductTemplate().getName())
                            .productTmplDescription(line.getProductTemplate().getDescription())
                    .build());

        }

        return MobileShoppingCartDTO.builder()
                .id(shoppingCart.getId())
                .shoppingLines(lines)
                .shipmentCost(shipmentCost)
                .subtotal(subtotal)
                .total(subtotal + shipmentCost)
                .build();
    }
}
