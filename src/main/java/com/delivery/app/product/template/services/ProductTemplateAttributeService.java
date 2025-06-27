package com.delivery.app.product.template.services;

import com.delivery.app.configs.constants.RoleType;
import com.delivery.app.configs.exception.common.ResourceNotFoundException;
import com.delivery.app.product.attribute.models.ProductAttribute;
import com.delivery.app.product.template.dtos.ProductTemplateAttributeDTO;
import com.delivery.app.product.template.models.ProductTemplate;
import com.delivery.app.product.template.repositories.ProductTemplateAttributeRepository;
import com.delivery.app.security.services.AuthenticationFacade;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ProductTemplateAttributeService {

    private final ProductTemplateAttributeRepository productTemplateAttributeRepository;
    private final AuthenticationFacade authenticationFacade;

    public List<ProductTemplateAttributeDTO> getAttributesByProductTmplId(
            Integer productTmplId
    ) {

        return productTemplateAttributeRepository.findByProductTmplId(productTmplId)
                .stream().map(r -> ProductTemplateAttributeDTO.builder()
                        .id(r.getId())
                        .name(r.getName())
                        .displayType(r.getDisplayType())
                        .sequence(r.getSequence())
                        .productTemplateId(Optional.ofNullable(r.getProductTemplate())
                                .map(ProductTemplate::getId)
                                .orElse(null))
                        .build())
                .toList();
    }

    @Transactional
    public ProductTemplateAttributeDTO create(ProductTemplateAttributeDTO attributeDTO) {

        ProductAttribute productAttribute = productTemplateAttributeRepository.save(
                ProductAttribute.builder()
                        .name(attributeDTO.name())
                        .displayType(attributeDTO.displayType())
                        //.restaurantTmpl(new RestaurantTemplate(getUserRestaurantId(attributeDTO.restaurantId())))
                        .sequence(attributeDTO.sequence())
                        .productTemplate(new ProductTemplate(attributeDTO.productTemplateId()))
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
