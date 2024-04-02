package com.delivery.app.product.template.services;

import com.delivery.app.product.template.dtos.ProductTemplateDTO;
import com.delivery.app.product.template.models.ProductTemplate;
import com.delivery.app.product.template.repositories.ProductTemplateRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class ProductTemplateService {

    private final ProductTemplateRepository productTemplateRepository;

    public ProductTemplateDTO findById(Integer tmplId) {

        return productTemplateRepository.findById(tmplId)
                .map(this::modelToProductTemplateDTO)
                .orElseThrow();
    }


    private ProductTemplateDTO modelToProductTemplateDTO(ProductTemplate template) {

        return  ProductTemplateDTO.builder()
                .id(template.getId())
                .name(template.getName())
                .description(template.getDescription())
                .restaurantId(template.getRestaurantId())
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
