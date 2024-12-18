package com.delivery.app.mobile.app.service;


import com.delivery.app.mobile.app.dto.MobileCategoryDTO;
import com.delivery.app.product.category.repository.ProductCategoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class MobileCategoryService {

    private final ProductCategoryRepository productCategoryRepository;

    public List<MobileCategoryDTO> loadAvailable() {

        return productCategoryRepository.findAll()
                .stream().map(r->new MobileCategoryDTO(r.getId(), r.getName()))
                .toList();
    }


}
