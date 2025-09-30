package com.delivery.app.product.template.services;

import com.delivery.app.configs.constants.RoleType;
import com.delivery.app.configs.exception.common.InvalidAccessResourceException;
import com.delivery.app.configs.exception.common.ResourceNotFoundException;
import com.delivery.app.product.template.dtos.ProductTemplateConfigDTO;
import com.delivery.app.product.template.models.ProductTemplate;
import com.delivery.app.product.template.repositories.ProductTemplateRepository;
import com.delivery.app.security.services.AuthenticationFacade;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@AllArgsConstructor
@Service
public class ProductTemplateConfigService {

    private final ProductTemplateRepository productTemplateRepository;
    private final AuthenticationFacade authenticationFacade;


    public ProductTemplateConfigDTO findConfigByProductTmplId(
            Integer tmplId
    ){


        ProductTemplateConfigDTO templateConfigDTO = productTemplateRepository.findById(tmplId)
                .map(this::modelToProductTemplateConfigDTO)
                .orElseThrow(() -> new ResourceNotFoundException("productTmpl", "id", tmplId));

        evaluateRestaurant(templateConfigDTO.restaurantId());

        return templateConfigDTO;
    }

    @Transactional
    public ProductTemplateConfigDTO updateConfig(ProductTemplateConfigDTO templateConfigDTO) {

        ProductTemplate productTemplate = productTemplateRepository.findById(templateConfigDTO.id())
                .orElseThrow(() -> new ResourceNotFoundException("productTmpl", "id", templateConfigDTO.id()));

        evaluateRestaurant(templateConfigDTO.restaurantId());

        productTemplate.updateConfig(templateConfigDTO);

        return ProductTemplateConfigDTO.builder()
                .id(productTemplate.getId())
                .build();
    }

    private ProductTemplateConfigDTO modelToProductTemplateConfigDTO(ProductTemplate template) {

        return  ProductTemplateConfigDTO.builder()
                .id(template.getId())
                .name(template.getName())
                .description(template.getDescription())
                .restaurantId(template.getRestaurantTmpl().getId())
                .listPrice(Optional.ofNullable(template.getListPrice()).orElse(0d))
                .categId(template.getCategory().getId())
                .salesOk(Optional.ofNullable(template.getSalesOK()).orElse(false))
                .active(Optional.ofNullable(template.getActive()).orElse(false))
                .picture(template.getPicture())
                .build();
    }

    private void evaluateRestaurant(Integer restaurantId) {

        String userRole = authenticationFacade.userRole();

        if(userRole.equals(RoleType.STORE.value())) {

            Integer userRestaurantId = authenticationFacade.storeUser();

            if(!userRestaurantId.equals(restaurantId)) {
                throw new InvalidAccessResourceException("restaurantTmpl", restaurantId);
            }
        }
    }

}
