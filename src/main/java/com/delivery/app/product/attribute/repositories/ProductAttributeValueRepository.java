package com.delivery.app.product.attribute.repositories;

import com.delivery.app.product.attribute.models.ProductAttributeValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductAttributeValueRepository extends JpaRepository<ProductAttributeValue, Integer> {

}
