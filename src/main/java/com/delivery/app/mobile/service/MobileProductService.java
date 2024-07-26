package com.delivery.app.mobile.service;


import com.delivery.app.configs.exception.common.ResourceNotFoundException;
import com.delivery.app.mobile.dtos.ProductFilterRequestDTO;
import com.delivery.app.mobile.dtos.ProductTemplateDetailDTO;
import com.delivery.app.mobile.dtos.ProductTemplateItemDTO;
import com.delivery.app.product.attribute.models.ProductAttributeValue;
import com.delivery.app.product.template.models.ProductTemplate;
import com.delivery.app.product.template.repositories.ProductTemplateRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class MobileProductService {

    private final ProductTemplateRepository productTemplateRepository;

    public Page<ProductTemplateItemDTO> loadProductTemplate(
            ProductFilterRequestDTO productFilterRequestDTO) {

        Random random = new Random();

        return productTemplateRepository.findAll(productFilterRequestDTO.pageable())
                .map(tmpl->ProductTemplateItemDTO.builder()
                        .id(tmpl.getId())
                        .name(tmpl.getName())
                        .listPrice(tmpl.getListPrice())
                        .picture(Optional.ofNullable(tmpl.getPicture()).orElse("https://coffee.alexflipnote.dev/random"))
                        .soldBy("Restaurant name")
                        .rate(random.nextInt(5 - 1 + 1) + 1)
                        .restaurantName(tmpl.getRestaurantTmpl().getName())
                        .build());
    }

    public ProductTemplateDetailDTO detail(Integer id) {

        ProductTemplate productTemplate = productTemplateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("template", "id", id));


        Random random = new Random();

        List<ProductTemplateDetailDTO.Attribute> attributes = getAttributes(productTemplate);


        Optional.ofNullable(attributes).ifPresent(a -> {
            Collections.sort(attributes);
        });


        return ProductTemplateDetailDTO.builder()
                .id(productTemplate.getId())
                .name(productTemplate.getName())
                .description(productTemplate.getDescription())
                .priceList(productTemplate.getListPrice())
                .picture(Optional.ofNullable(productTemplate.getPicture()).orElse("https://coffee.alexflipnote.dev/random"))
                .rate(random.nextInt(5 - 1 + 1) + 1)
                .restaurant(ProductTemplateDetailDTO.Restaurant.builder()
                        .name(productTemplate.getRestaurantTmpl().getName())
                        .picture("https://coffee.alexflipnote.dev/random")
                        .build())
                .attributes(
                        Optional.ofNullable(attributes)
                                .map(att -> att.stream().peek(
                                        attrVal -> attrVal.setAttributeValues(
                                                productTemplate.getAttributeValues().stream()
                                                        .filter(i -> i.getAttribute().getId().equals(attrVal.getId()))
                                                        .sorted(Comparator.comparing(ProductAttributeValue::getSequence))
                                                        .map(val -> ProductTemplateDetailDTO.AttributeValue.builder()
                                                                .id(val.getId())
                                                                .extraPrice(val.getExtraPrice())
                                                                .name(val.getName())
                                                                .picture("https://coffee.alexflipnote.dev/random")
                                                                .build())
                                                        .collect(Collectors.toList())
                                        )).collect(Collectors.toList())
                                ).orElse(null)
                ).build();
    }

    private static List<ProductTemplateDetailDTO.Attribute> getAttributes(ProductTemplate productTemplate) {
        return Optional.ofNullable(productTemplate.getAttributeValues()).map(
                attributeValues -> attributeValues.stream()
                        .collect(Collectors.groupingBy(ProductAttributeValue::getAttribute))
                        .keySet()
                        .stream().map(s -> ProductTemplateDetailDTO.Attribute.builder()
                                .id(s.getId())
                                .name(s.getName())
                                .displayType(s.getDisplayType())
                                .sequence(s.getSequence())
                                .build())
                        .collect(Collectors.toList())
        ).orElse(null);
    }

}


