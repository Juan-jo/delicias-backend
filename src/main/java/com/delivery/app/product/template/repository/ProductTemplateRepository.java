package com.delivery.app.product.template.repository;

import com.delivery.app.product.template.model.ProductTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductTemplateRepository extends JpaRepository<ProductTemplate, Integer> {

}
