package com.delivery.app.product.template.services;

import com.delivery.app.configs.constants.RoleType;
import com.delivery.app.configs.exception.common.ResourceNotFoundException;
import com.delivery.app.product.attribute.models.ProductAttribute;
import com.delivery.app.product.template.dtos.ProductTemplateAttributeDTO;
import com.delivery.app.product.template.repositories.ProductTemplateAttributeRepository;
import com.delivery.app.restaurant.template.model.RestaurantTemplate;
import com.delivery.app.security.services.AuthenticationFacade;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class ProductTemplateAttributeService {

    private final ProductTemplateAttributeRepository productTemplateAttributeRepository;
    private final AuthenticationFacade authenticationFacade;

    public List<ProductTemplateAttributeDTO> getAttributesByRestaurantId(
            Integer restaurantId
    ) {

        return productTemplateAttributeRepository.findByRestaurantTmplId(restaurantId)
                .stream().map(r -> ProductTemplateAttributeDTO.builder()
                        .id(r.getId())
                        .name(r.getName())
                        .displayType(r.getDisplayType())
                        .sequence(r.getSequence())
                        .build())
                .toList();
    }

    @Transactional
    public ProductTemplateAttributeDTO create(ProductTemplateAttributeDTO attributeDTO) {

        ProductAttribute productAttribute = productTemplateAttributeRepository.save(
                ProductAttribute.builder()
                        .name(attributeDTO.name())
                        .displayType(attributeDTO.displayType())
                        .restaurantTmpl(new RestaurantTemplate(getUserRestaurantId(attributeDTO.restaurantId())))
                        .sequence(attributeDTO.sequence())
                        .build()
        );

        return ProductTemplateAttributeDTO.builder()
                .id(productAttribute.getId())
                .build();
    }

    @Transactional
    public void update(ProductTemplateAttributeDTO attributeDTO) {

        ProductAttribute productAttribute = productTemplateAttributeRepository.findById(attributeDTO.id())
                .orElseThrow(() -> new ResourceNotFoundException("tmplAttr", "id", attributeDTO.id()));

        productAttribute.update(attributeDTO);
    }

    @Transactional
    public void delete(Integer id) {

        ProductAttribute productAttribute = productTemplateAttributeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("tmplAttr", "id", id));

        productTemplateAttributeRepository.delete(productAttribute);

    }


    private Integer getUserRestaurantId(Integer restaurantId) {

        String userRole = authenticationFacade.userRole();

        if(userRole.equals(RoleType.STORE.value())) {

            restaurantId = authenticationFacade.storeUser();
        }

        return restaurantId;
    }
}
