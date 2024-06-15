package com.delivery.app.product.template.services;

import com.delivery.app.product.template.dtos.ProductTemplateDTO;
import com.delivery.app.product.template.dtos.ProductTemplateReqFilterRowsDTO;
import com.delivery.app.product.template.dtos.ProductTemplateRowDTO;
import com.delivery.app.product.template.models.ProductTemplate;
import com.delivery.app.product.template.repositories.ProductTemplateRepository;
import com.delivery.app.security.services.AuthenticationFacade;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class ProductTemplateService {

    private final ProductTemplateRepository productTemplateRepository;
    private final AuthenticationFacade authenticationFacade;

    public ProductTemplateDTO findById(Integer tmplId) {

        return productTemplateRepository.findById(tmplId)
                .map(this::modelToProductTemplateDTO)
                .orElseThrow();
    }

    public Page<ProductTemplateRowDTO> searchFilter(ProductTemplateReqFilterRowsDTO reqFilterRowsDTO){

        Integer restaurantId = authenticationFacade.storeUser();

        return productTemplateRepository.searchFilter(
                StringUtils.upperCase(reqFilterRowsDTO.getName()),
                restaurantId,
                reqFilterRowsDTO.pageable()
        ).map(r-> ProductTemplateRowDTO.builder()
                .id(r.getId())
                .name(r.getName())
                .restaurantName(r.getRestaurantTmpl().getName())
                .categName(r.getCategory().getName())
                .createdAt(r.getCreatedAt())
                .updatedAt(r.getUpdatedAt())
                .build());
    }

    private ProductTemplateDTO modelToProductTemplateDTO(ProductTemplate template) {

        return  ProductTemplateDTO.builder()
                .id(template.getId())
                .name(template.getName())
                .description(template.getDescription())
                .restaurantId(template.getRestaurantTmpl().getId())
                .listPrice(template.getListPrice())
                .categId(template.getCategory().getId())
                .salesOk(template.getSalesOK())
                .active(template.getActive())
                .hasConfigurableAttributes(template.getHasConfigurableAttributes())
                .attributes(template.getAttributeProductTemplateRels().stream()
                        .map(i -> ProductTemplateDTO.Attribute.builder()
                                .id(i.getId().getAttribute().getId())
                                .name(i.getId().getAttribute().getName())
                                .build())
                        .collect(Collectors.toSet()))
                .attributeValues(template.getAttributeValues().stream()
                        .map(i-> ProductTemplateDTO.AttributeValue.builder()
                                .id(i.getId())
                                .sequence(i.getSequence())
                                .name(i.getName())
                                .extraPrice(i.getExtraPrice())
                                .attributeId(i.getAttribute().getId())
                                .build())
                        .collect(Collectors.toSet()))
                .build();
    }

}
