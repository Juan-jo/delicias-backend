package com.delivery.app.product.template.repositories;

import com.delivery.app.product.attribute.models.ProductAttribute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductTemplateAttributeRepository extends JpaRepository<ProductAttribute, Integer> {


    @Query("SELECT p FROM ProductAttribute p WHERE p.productTemplate.id = :tmplId ORDER BY p.sequence ASC")
    List<ProductAttribute> findByProductTmplId(Integer tmplId);
}
