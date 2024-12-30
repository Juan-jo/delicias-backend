package com.delivery.app.mobile.user.service;

import com.delivery.app.configs.exception.common.ResourceNotFoundException;
import com.delivery.app.mobile.user.dtos.MobileAddShoppingCartLineDTO;
import com.delivery.app.mobile.user.models.ShoppingCart;
import com.delivery.app.mobile.user.models.ShoppingCartLine;
import com.delivery.app.mobile.user.repository.ShoppingCartLineRepository;
import com.delivery.app.mobile.user.repository.ShoppingCartRepository;
import com.delivery.app.product.template.models.ProductTemplate;
import com.delivery.app.product.template.repositories.ProductTemplateRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@AllArgsConstructor
@Service
public class MobileShoppingCartLineService {

    private final ProductTemplateRepository productTemplateRepository;
    private final ShoppingCartRepository shoppingCartRepository;
    private final ShoppingCartLineRepository shoppingCartLineRepository;

    @Transactional
    public void addShoppingCartLine(MobileAddShoppingCartLineDTO shoppingCartLineDTO) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID userUid = UUID.fromString(authentication.getName());

        ProductTemplate productTemplate = productTemplateRepository.findById(shoppingCartLineDTO.productTmplId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", shoppingCartLineDTO.productTmplId()));

        ShoppingCart shoppingCart = shoppingCartRepository.findByUserUIDAndRestaurantId(
                userUid,
                productTemplate.getRestaurantTmpl().getId()
        ).orElse(null);


        if(shoppingCart == null) {
            shoppingCart = ShoppingCart.builder()
                    .userUID(userUid)
                    .restaurant(productTemplate.getRestaurantTmpl())
                    .build();
            shoppingCartRepository.save(shoppingCart);
        }

        shoppingCartLineRepository.save(
                ShoppingCartLine.builder()
                        .shoppingCart(shoppingCart)
                        .qty(shoppingCartLineDTO.qty())
                        .productTemplate(productTemplate)
                        .attrValuesIds(shoppingCartLineDTO.attrValues())
                        .build()
        );

    }
}
