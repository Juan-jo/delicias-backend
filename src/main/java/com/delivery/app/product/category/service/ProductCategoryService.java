package com.delivery.app.product.category.service;

import com.delivery.app.product.category.dtos.ProductCategoryRecordDTO;
import com.delivery.app.product.category.model.ProductCategory;
import com.delivery.app.product.category.repository.ProductCategoryRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
                    .orElseThrow();

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
                .orElseThrow();

        category.setName(categoryRecord.name());

        Optional.ofNullable(categoryRecord.parentId()).ifPresentOrElse(parentId -> {

            ProductCategory parent = productCategoryRepository.findById(parentId)
                    .orElseThrow();

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

        return  productCategoryRepository.findById(id)
                .map(this::modelToProductCategoryDTO)
                .orElseThrow();
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
