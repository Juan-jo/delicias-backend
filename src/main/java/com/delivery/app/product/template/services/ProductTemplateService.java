package com.delivery.app.product.template.services;

import com.delivery.app.configs.DeliciasAppProperties;
import com.delivery.app.configs.constants.RoleType;
import com.delivery.app.configs.exception.common.ResourceNotFoundException;
import com.delivery.app.product.category.model.ProductCategory;
import com.delivery.app.product.template.dtos.*;
import com.delivery.app.product.template.models.ProductTemplate;
import com.delivery.app.product.template.repositories.ProductTemplateRepository;
import com.delivery.app.product.template.repositories.ViewProductTmplAttrValueRepository;
import com.delivery.app.restaurant.template.model.RestaurantTemplate;
import com.delivery.app.security.services.AuthenticationFacade;
import com.delivery.app.utils.DeliciasFileUtils;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class ProductTemplateService {

    private final ProductTemplateRepository productTemplateRepository;
    private final AuthenticationFacade authenticationFacade;
    private final ViewProductTmplAttrValueRepository viewProductTmplAttrValueRepository;

    private final DeliciasFileUtils deliciasFileUtils;
    private final DeliciasAppProperties deliciasAppProperties;

    public ProductTemplateDTO findById(Integer tmplId) {

        return productTemplateRepository.findById(tmplId)
                .map(this::modelToProductTemplateDTO)
                .orElseThrow();
    }

    @Transactional
    public ProductTemplateRecordDTO create(
            ProductTemplateRecordDTO productTemplateRecordDTO
    ) {

        Integer restaurantId = productTemplateRecordDTO.restaurantId();
        String userRole = authenticationFacade.userRole();

        if(userRole.equals(RoleType.STORE.value())) {
            restaurantId = authenticationFacade.storeUser();
        }

        ProductTemplate productTemplate = productTemplateRepository.save(
                ProductTemplate.builder()
                        .name(productTemplateRecordDTO.name())
                        .category(new ProductCategory(productTemplateRecordDTO.categId()))
                        .restaurantTmpl(new RestaurantTemplate(restaurantId))
                        .build()
        );

        return ProductTemplateRecordDTO.builder()
                .id(productTemplate.getId())
                .build();
    }

    @Transactional
    public ProductTemplateRecordDTO update(
            ProductTemplateRecordDTO productTemplateRecordDTO
    ) {

        ProductTemplate productTemplate = productTemplateRepository.findById(productTemplateRecordDTO.id())
                .orElseThrow(() -> new ResourceNotFoundException("productTmpl", "id", productTemplateRecordDTO.id()));

        productTemplate.updateBasicInfo(productTemplateRecordDTO);

        return ProductTemplateRecordDTO.builder()
                .id(productTemplate.getId())
                .build();
    }

    @Transactional
    public Map<String, String> uploadFile(Integer productTmplId, MultipartFile file) throws IOException {

        ProductTemplate productTemplate = productTemplateRepository.findById(productTmplId)
                .orElseThrow(() -> new ResourceNotFoundException("productTmpl", "id", productTmplId));


        String fileName = deliciasFileUtils.saveFile(file);

        productTemplate.setPicture(fileName);

        return Map.of("picture", String.format("%s/%s",deliciasAppProperties.getFiles().getResources(), fileName));
    }

    public List<ProductTmplAttributeRowDTO> attributeValuesRowDTOS(Integer productTmplId) {


        return viewProductTmplAttrValueRepository.findByProductTmplId(productTmplId)
                .stream().map(r-> ProductTmplAttributeRowDTO.builder()
                        .id(r.getId())
                        .attributeValue(r.getNameAttr() + " - " + r.getNameAttrValue())
                        .sequence(r.getSequenceAttrValue())
                        .extraPrice(r.getExtraPrice())
                        .displayType(r.getDisplayType())
                        .build()).toList();
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
                .picture(
                        Optional.ofNullable(r.getPicture())
                                .map(c->String.format("%s/%s", deliciasAppProperties.getFiles().getResources(), c))
                                .orElse(deliciasAppProperties.getFiles().getStaticDefault())
                )
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
