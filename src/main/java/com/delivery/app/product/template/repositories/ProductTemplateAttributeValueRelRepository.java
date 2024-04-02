package com.delivery.app.product.template.repositories;

import com.delivery.app.product.template.models.ProductTemplateAttributeValueRel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductTemplateAttributeValueRelRepository extends JpaRepository<ProductTemplateAttributeValueRel, Integer> {

}
