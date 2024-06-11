package com.delivery.app.product.category.service;

import com.delivery.app.configs.exception.common.ResourceNotFoundException;
import com.delivery.app.product.category.dto.ProductCategoryParentOptionDTO;
import com.delivery.app.product.category.dto.ProductCategoryRecordDTO;
import com.delivery.app.product.category.dto.ProductCategoryReqFilterRows;
import com.delivery.app.product.category.dto.ProductCategoryRow;
import com.delivery.app.product.category.model.ProductCategory;
import com.delivery.app.product.category.repository.ProductCategoryRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@AllArgsConstructor
@Service
public class ProductCategoryService {

    private final ProductCategoryRepository productCategoryRepository;


    @Transactional
    public ProductCategoryRecordDTO create(ProductCategoryRecordDTO categoryRecord) {

        ProductCategory.ProductCategoryBuilder productCategory  = ProductCategory.builder()
                .name(categoryRecord.name())
                .completeName(categoryRecord.name());

        Optional.ofNullable(categoryRecord.parentId()).ifPresent(parentId -> {

            ProductCategory parent = productCategoryRepository.findById(parentId)
                    .orElseThrow(() -> new ResourceNotFoundException("categ", "parentId", parentId));

            productCategory.completeName(String.format("%s / %s", parent.getCompleteName(), categoryRecord.name()));
            productCategory.parent(parent);

        });

        ProductCategory newCategory = productCategoryRepository.save(productCategory.build());

        newCategory.setParentPath(
                calculateParentPath(newCategory.getParent(), newCategory.getId())
        );

        return modelToProductCategoryDTO(newCategory);
    }


    @Transactional
    public ProductCategoryRecordDTO update(ProductCategoryRecordDTO categoryRecord) {

        ProductCategory category = productCategoryRepository.findById(categoryRecord.id())
                .orElseThrow(() -> new ResourceNotFoundException("categ", "id", categoryRecord.id()));

        category.setName(categoryRecord.name());

        Optional.ofNullable(categoryRecord.parentId()).ifPresentOrElse(parentId -> {

            ProductCategory parent = productCategoryRepository.findById(parentId)
                    .orElseThrow(() -> new ResourceNotFoundException("categ", "parentId", parentId));

            category.setCompleteName(String.format("%s / %s", parent.getCompleteName(), categoryRecord.name()));
            category.setParent(parent);

        }, () -> {

            category.setCompleteName(categoryRecord.name());
            category.setParent(null);

        });

        category.setParentPath(
                calculateParentPath(category.getParent(), category.getId())
        );

        return modelToProductCategoryDTO(category);
    }

    public ProductCategoryRecordDTO findById(Integer id) {

        return productCategoryRepository.findById(id)
                .map(this::modelToProductCategoryDTO)
                .orElseThrow(() -> new ResourceNotFoundException("categ", "id", id));
    }

    @Transactional(readOnly = true)
    public Page<ProductCategoryRow> findByFilter(
            ProductCategoryReqFilterRows reqFilterRows
    ) {

        return productCategoryRepository.findByFilter(
                StringUtils.upperCase(reqFilterRows.getName()),
                reqFilterRows.pageable()
        ).map(r -> ProductCategoryRow.builder()
                .id(r.getId())
                .name(r.getName())
                .completeName(r.getCompleteName())
                .createdAt(r.getCreatedAt())
                .updatedAt(r.getUpdatedAt())
                .build());
    }

    @Transactional(readOnly = true)
    public List<ProductCategoryParentOptionDTO> getParents() {
        return this.productCategoryRepository.findAll()
                .stream().map(r -> ProductCategoryParentOptionDTO.builder()
                        .id(r.getId())
                        .completeName(r.getCompleteName())
                        .build())
                .toList();
    }

    public void deleteById(Integer id) {
        ProductCategory category = productCategoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("categ", "id", id));

        productCategoryRepository.delete(category);
    }

    private String calculateParentPath(ProductCategory parent, Integer newCategId) {

        StringBuilder sb = new StringBuilder();

        while(parent != null) {

            sb.insert(0, String.format("%s/", parent.getId()));
            parent = parent.getParent();
        }

        sb.append(
                String.format("%s/", newCategId)
        );

        return sb.toString();
    }

    private ProductCategoryRecordDTO modelToProductCategoryDTO(ProductCategory category) {
        return ProductCategoryRecordDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .parentId(
                        Optional.ofNullable(category.getParent()).map(ProductCategory::getId).orElse(null)
                )
                .build();
    }
}
