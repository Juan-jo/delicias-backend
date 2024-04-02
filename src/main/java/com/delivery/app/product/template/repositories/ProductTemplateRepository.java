package com.delivery.app.product.template.repositories;

import com.delivery.app.product.template.models.ProductTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductTemplateRepository extends JpaRepository<ProductTemplate, Integer> {

}
