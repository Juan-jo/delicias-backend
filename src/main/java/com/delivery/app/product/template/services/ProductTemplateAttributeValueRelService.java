package com.delivery.app.product.template.services;

import com.delivery.app.configs.exception.common.ResourceNotFoundException;
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
    public void create(
            ProductTemplateAttributeValueDTO attributeValueDTO) {

        ProductAttributeValue attributeValue = productAttributeValueRepository.save(
                ProductAttributeValue.builder()
                        .name(attributeValueDTO.name())
                        .extraPrice(attributeValueDTO.extraPrice())
                        .sequence(attributeValueDTO.sequence())
                        .attribute(new ProductAttribute(attributeValueDTO.attributeId()))
                        .build()
        );

        templateAttributeValueRelRepository.save(
                ProductTemplateAttributeValueRel.builder()
                        .attributeValue(attributeValue)
                        .attribute(new ProductAttribute(attributeValueDTO.attributeId()))
                        .template(new ProductTemplate(attributeValueDTO.productTmplId()))
                        .active(true)
                        .build()
        );
    }

    @Transactional(readOnly = true)
    public ProductTemplateAttributeValueDTO findById(Integer id) {

        return productAttributeValueRepository.findById(id)
                .map(this::modelToProductTemplateAttributeValueDTO)
                .orElseThrow(() -> new ResourceNotFoundException("attrVal","id",id));
    }

    @Transactional
    public void delete(Integer id) {

        templateAttributeValueRelRepository.deleteByAttributeValueId(id);
        productAttributeValueRepository.deleteById(id);
    }

    @Transactional
    public void update(
            ProductTemplateAttributeValueDTO attributeValueDTO) {

        ProductAttributeValue attributeValueRel =
                productAttributeValueRepository.findById(attributeValueDTO.id())
                        .orElseThrow(() -> new ResourceNotFoundException("attrVal", "id", attributeValueDTO.id()));

        ProductTemplateAttributeValueRel valueRel =
                templateAttributeValueRelRepository.findByTemplateIdAndAttributeValueId(attributeValueDTO.productTmplId(), attributeValueDTO.id())
                                .orElseThrow(() -> new ResourceNotFoundException("", "", ""));

        valueRel.setAttribute(new ProductAttribute(attributeValueDTO.attributeId()));

        attributeValueRel.update(attributeValueDTO);
    }


    private ProductTemplateAttributeValueDTO modelToProductTemplateAttributeValueDTO(
            ProductAttributeValue valueRel
    ) {

        return ProductTemplateAttributeValueDTO.builder()
                .id(valueRel.getId())
                .attributeId(valueRel.getAttribute().getId())
                .name(valueRel.getName())
                .extraPrice(valueRel.getExtraPrice())
                .sequence(valueRel.getSequence())
                .build();
    }

}
