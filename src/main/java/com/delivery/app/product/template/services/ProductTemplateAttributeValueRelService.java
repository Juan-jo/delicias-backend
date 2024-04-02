package com.delivery.app.product.template.services;

import com.delivery.app.product.attribute.models.ProductAttribute;
import com.delivery.app.product.attribute.models.ProductAttributeValue;
import com.delivery.app.product.attribute.repositories.ProductAttributeValueRepository;
import com.delivery.app.product.template.dtos.ProductTemplateAttributeValueDTO;
import com.delivery.app.product.template.models.ProductTemplate;
import com.delivery.app.product.template.models.ProductTemplateAttributeValueRel;
import com.delivery.app.product.template.repositories.ProductTemplateAttributeValueRelRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
@Service
public class ProductTemplateAttributeValueRelService {

    private final ProductTemplateAttributeValueRelRepository templateAttributeValueRelRepository;
    private final ProductAttributeValueRepository productAttributeValueRepository;

    @Transactional
    public ProductTemplateAttributeValueDTO create(
            ProductTemplateAttributeValueDTO attributeValueDTO) {

        ProductAttributeValue attributeValue = productAttributeValueRepository.save(
                ProductAttributeValue.builder()
                        .name(attributeValueDTO.name())
                        .extraPrice(attributeValueDTO.extraPrice())
                        .sequence(attributeValueDTO.sequence())
                        .attribute(new ProductAttribute(attributeValueDTO.attributeId()))
                        .build()
        );

        ProductTemplateAttributeValueRel valueRel = templateAttributeValueRelRepository.save(
                ProductTemplateAttributeValueRel.builder()
                        .attributeValue(attributeValue)
                        .attribute(new ProductAttribute(attributeValueDTO.attributeId()))
                        .template(new ProductTemplate(attributeValueDTO.productTmplId()))
                        .active(true)
                        .build()
        );

        return modelToProductTemplateAttributeValueDTO(valueRel);
    }

    private ProductTemplateAttributeValueDTO modelToProductTemplateAttributeValueDTO(
            ProductTemplateAttributeValueRel valueRel
    ) {

        return ProductTemplateAttributeValueDTO.builder()
                .id(valueRel.getId())
                .build();
    }

}
